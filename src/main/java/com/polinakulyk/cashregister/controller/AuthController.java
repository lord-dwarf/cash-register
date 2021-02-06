package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.LoginRequestDto;
import com.polinakulyk.cashregister.controller.dto.LoginResponseDto;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.service.vo.UserDetailsVo;
import com.polinakulyk.cashregister.util.CashRegisterSecurityUtil;
import java.net.http.HttpResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CashRegisterSecurityUtil securityUtil;

    public AuthController(
            AuthenticationManager authenticationManager, CashRegisterSecurityUtil securityUtil) {
        this.authenticationManager = authenticationManager;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/login")
    public @ResponseBody
    LoginResponseDto loginUser(@RequestBody LoginRequestDto loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLogin(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // TODO ensure that after auth manager really clears the password
        String userId = ((UserDetailsVo) auth.getPrincipal()).getUsername();
        String userRole = securityUtil.getUserRoleFromAuthRoles(auth.getAuthorities());
        String jwt = securityUtil.createJwt(userId, userRole);

        return new LoginResponseDto()
                .setJwt(jwt)
                .setUser(new User()
                        .setId(userId)
                        .setRole(userRole)
                        .setReceipts(null));
    }

    @PostMapping("/logout")
    public @ResponseBody String logoutUser(HttpServletResponse response) {
        response.setStatus(NO_CONTENT.value());
        return "";
    }

}