package com.polinakulyk.cashregister.controller.filter;

import com.polinakulyk.cashregister.controller.dto.JwtDto;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.util.CashRegisterSecurityUtil;
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

    private final CashRegisterSecurityUtil securityUtil;
    private final UserService userService;

    public JwtAuthFilter(CashRegisterSecurityUtil securityUtil, UserService userService) {
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // if JWT is present then validate it and parse and get actual user details,
        // in the end put the resulting Authentication object into the security context
        Optional<String> jwtOpt = securityUtil.getJwtFromRequest(request);
        if (jwtOpt.isPresent() && securityUtil.validateJwt(jwtOpt.get())) {
            JwtDto jwt = securityUtil.parseJwt(jwtOpt.get());
            Authentication auth = getAuthenticationFromDbByJwt(jwt);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthenticationFromDbByJwt(JwtDto jwt) {

        // TODO move this method + dependency on a service, into filter
        // TODO cache retrieval of user from DB, because currently it happens on each request
        User user = userService.findById(jwt.getUserId()).orElseThrow(() ->
                new CashRegisterException(
                        HttpStatus.UNAUTHORIZED,
                        quote("User not found", jwt.getUserId())));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getId(), "", securityUtil.getAuthRolesFromUserRole(user.getRole()));
        // TODO include user name into Authentication object
        auth.setDetails("");
        return auth;
    }
}
