package com.polinakulyk.cashregister.security.filter;

import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.JwtDto;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.service.api.UserService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthHelper authHelper;
    private final UserService userService;

    public JwtAuthFilter(AuthHelper authHelper, UserService userService) {
        this.authHelper = authHelper;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // If JWT is present in request header, then validate JWT and parse it,
        // and get actual user details from DB.
        // In the end, put the resulting Authentication object into the Spring security context.
        Optional<String> jwtOpt = authHelper.getJwtFromRequest(request);
        if (jwtOpt.isPresent() && authHelper.validateJwt(jwtOpt.get())) {
            JwtDto jwt = authHelper.parseJwt(jwtOpt.get());
            Authentication auth = getAuthenticationFromDbByJwt(jwt);
            authHelper.setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthenticationFromDbByJwt(JwtDto jwt) {

        User user = userService.findExistingById(jwt.getUserId());

        UserDetailsDto principal = new UserDetailsDto()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setPassword("")
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, "", authHelper.getAuthRolesFromUserRole(user.getRole()));
        auth.setDetails("");

        return auth;
    }
}
