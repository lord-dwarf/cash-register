package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.LoginRequestDto;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public @ResponseBody
    LoginResponseDto loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto.getLogin(), loginRequestDto.getPassword());
    }

    @PostMapping("/logout")
    public @ResponseBody
    Map logoutUser(@RequestBody Map emptyRequestBody) {
        return Map.of(); // always succeed thanks to JWT
    }
}