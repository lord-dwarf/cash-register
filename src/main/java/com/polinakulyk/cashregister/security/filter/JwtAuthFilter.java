package com.polinakulyk.cashregister.security.filter;

import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.JwtDto;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.service.api.UserService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

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

        // if JWT is present then validate it and parse and get actual user details,
        // in the end put the resulting Authentication object into the security context
        Optional<String> jwtOpt = authHelper.getJwtFromRequest(request);
        if (jwtOpt.isPresent() && authHelper.validateJwt(jwtOpt.get())) {
            JwtDto jwt = authHelper.parseJwt(jwtOpt.get());
            Authentication auth = getAuthenticationFromDbByJwt(jwt);
            authHelper.setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthenticationFromDbByJwt(JwtDto jwt) {

        // TODO cache retrieval of user from DB, because currently it happens on each request
        User user = userService.findById(jwt.getUserId()).orElseThrow(() ->
                new CashRegisterException(
                        HttpStatus.FORBIDDEN,
                        quote("User not found", jwt.getUserId())));

        UserDetailsDto principal = new UserDetailsDto()
                .setUsername(user.getId())
                .setPassword("")
                .setPrincipalName(user.getUsername())
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, "", authHelper.getAuthRolesFromUserRole(user.getRole()));
        auth.setDetails("");
        return auth;
    }
}
