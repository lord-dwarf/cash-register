package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.CashRegisterApplication;
import com.polinakulyk.cashregister.controller.dto.LoginRequestDto;
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromMvcResultToBodyPojo;
import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromPojoToString;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {CashRegisterApplication.class})
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.properties")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    public void loginUser_happy() throws Exception {
        var result = mvc.perform(
                post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(fromPojoToString(om, new LoginRequestDto()
                                .setLogin("sr-teller")
                                .setPassword("Sr-tellerpass,0"))
                        )
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var loginResponseDto = fromMvcResultToBodyPojo(result, om, LoginResponseDto.class);
        assertThat(loginResponseDto).isNotNull();
        assertThat(loginResponseDto.getJwt()).isNotEmpty();

        var user = loginResponseDto.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isNotEmpty();
        assertThat(user.getFullName()).isNotEmpty();
        assertThat(user.getPassword()).isNull();
    }
}
