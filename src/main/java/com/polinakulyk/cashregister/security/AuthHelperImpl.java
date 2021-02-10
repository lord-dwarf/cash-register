package com.polinakulyk.cashregister.security;

import com.polinakulyk.cashregister.security.dto.JwtDto;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.util.CashRegisterUtil;
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

import static com.polinakulyk.cashregister.util.CashRegisterUtil.*;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.from;

@Component
public class AuthHelperImpl implements AuthHelper {

    private static final String JWT_CLAIM_ROLE = "role";
    private static final String JWT_AUTH_HEADER = "Authorization";
    private static final String JWT_BEARER_PREFIX = "Bearer ";
    private static final String SPRING_ROLE_PREFIX = "ROLE_";

    @Value("${cashregister.jwt.secret}")
    private String jwtSecret;

    @Value("${cashregister.jwt.expire_sec}")
    private int jwtExpirationMs;

    @PostConstruct
    public void init() {

        // TODO validate config
        // TODO log config (don't expose secrets)
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO explicitly configure BCrypt encoder
        return new BCryptPasswordEncoder();
    }

    @Override
    public String createJwt(String userId, String role) {

        // TODO put Issuer into JWT (that identifies CashRegister as issuer)
        // TODO put Audience into JWT (that identifies JWT as CashRegister's auth JWT)
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put(JWT_CLAIM_ROLE, getAuthRolesFromUserRole(role));

        LocalDateTime now = now();
        LocalDateTime exp = now.plusSeconds(jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(from(now))
                .setExpiration(from(exp))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    @Override
    public boolean validateJwt(String jwt) {

        // TODO ensure exceptions result in HTTP 4xx
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
        return Optional.ofNullable(CashRegisterUtil.nonEmpty(jwt));
    }

    @Override
    public JwtDto parseJwt(String jwt) {

        // TODO ensure exceptions result in HTTP 4xx
        Jws<Claims> jwtParsed = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
        return new JwtDto()
                .setUserId(jwtParsed.getBody().getSubject())
                .setRole(jwtParsed.getBody().get(JWT_CLAIM_ROLE, List.class).get(0).toString())
                .setIssuedAt(from(jwtParsed.getBody().getIssuedAt()))
                .setExpireAt(from(jwtParsed.getBody().getExpiration()));
    }

    @Override
    public List<GrantedAuthority> getAuthRolesFromUserRole(String userRole) {
        return List.of(new SimpleGrantedAuthority(SPRING_ROLE_PREFIX + userRole));
    }

    @Override
    public String getUserRoleFromAuthRoles(Collection<? extends GrantedAuthority> authRoles) {
        return new ArrayList<>(authRoles).get(0)
                .getAuthority().replaceFirst(SPRING_ROLE_PREFIX, "");
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
