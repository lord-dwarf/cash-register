package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.LoginRequestDto;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;

import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthHelper authHelper;

    public AuthController(UserService userService, AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;
    }

    @PostMapping("/login")
    public @ResponseBody
    LoginResponseDto loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto.getLogin(), loginRequestDto.getPassword());
    }

    @PostMapping("/logout")
    public @ResponseBody
    Map logoutUser(@RequestBody Map emptyRequestBody) {
        log.info("DONE Logout of user '{}'", authHelper.getUserId());
        return Map.of(); // always succeed thanks to JWT
    }
}