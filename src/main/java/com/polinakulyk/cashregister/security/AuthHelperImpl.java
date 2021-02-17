package com.polinakulyk.cashregister.security;

import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.JwtDto;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.security.dto.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.from;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.now;

@Component
public class AuthHelperImpl implements AuthHelper {

    private static final String JWT_CLAIM_ROLE = "role";
    private static final String JWT_AUTH_HEADER = "Authorization";
    private static final String JWT_BEARER_PREFIX = "Bearer ";
    private static final String SPRING_ROLE_PREFIX = "ROLE_";

    @Value("${cashregister.jwt.secret}")
    private String jwtSecret;

    @Value("${cashregister.jwt.expire_sec}")
    private int jwtExpirationSeconds;

    @PostConstruct
    public void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates JWT based on user id (not username!) and role.
     *
     * @param userId
     * @param role
     * @return
     */
    @Override
    public String createJwt(String userId, UserRole role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put(JWT_CLAIM_ROLE, getAuthRolesFromUserRole(role));

        LocalDateTime now = now();
        LocalDateTime exp = now.plusSeconds(jwtExpirationSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(from(now))
                .setExpiration(from(exp))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    @Override
    public boolean validateJwt(String jwt) {
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
        return true;
    }

    @Override
    public Optional<String> getJwtFromRequest(HttpServletRequest req) {
        // TODO move JWT from header into cookie because of Security
        String jwt = req.getHeader(JWT_AUTH_HEADER);
        if (jwt != null && jwt.startsWith(JWT_BEARER_PREFIX)) {
            jwt = jwt.substring(JWT_BEARER_PREFIX.length());
        }
        return Optional.ofNullable(jwt != null && !jwt.isEmpty() ? jwt : null);
    }

    @Override
    public JwtDto parseJwt(String jwt) {
        Jws<Claims> jwtParsed = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
        var body = jwtParsed.getBody();
        return new JwtDto()
                .setUserId(body.getSubject())
                .setRole(body.get(JWT_CLAIM_ROLE, List.class).get(0).toString())
                .setIssuedAt(from(body.getIssuedAt()))
                .setExpireAt(from(body.getExpiration()));
    }

    /**
     * Convert {@link UserRole} into Spring Security roles.
     *
     * @param userRole
     * @return
     */
    @Override
    public List<GrantedAuthority> getAuthRolesFromUserRole(UserRole userRole) {
        return List.of(new SimpleGrantedAuthority(SPRING_ROLE_PREFIX + userRole));
    }

    @Override
    public UserRole getUserRoleFromAuthRoles(Collection<? extends GrantedAuthority> authRoles) {
        String userRoleStr = new ArrayList<>(authRoles).get(0)
                .getAuthority().replaceFirst(SPRING_ROLE_PREFIX, "");
        return UserRole.fromString(userRoleStr).orElseThrow();
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public String getUserId() {
        Authentication auth = getAuthentication();
        return ((UserDetailsDto) auth.getPrincipal()).getUserId();
    }

    @Override
    public String getUsername() {
        Authentication auth = getAuthentication();
        return ((UserDetailsDto) auth.getPrincipal()).getUsername();
    }
}
