package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.controller.dto.LoginRequestDto;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.UserRole;
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerIntegrationTestHelper {

    private ControllerIntegrationTestHelper() {
        throw new UnsupportedOperationException("Can not instantiate");
    }

    public static String authenticateUsingApi(
            MockMvc mvc, ObjectMapper om, UserRole userRole) throws Exception {
        switch (userRole) {
            case SR_TELLER:
                return authenticateUsingApi(mvc, om, "sr-teller", "Sr-tellerpass,0");
            case TELLER:
                return authenticateUsingApi(mvc, om, "teller", "Tellerpass,0");
            case MERCH:
                return authenticateUsingApi(mvc, om, "merch", "Merchpass,0");
            default:
                throw new UnsupportedOperationException(
                        quote("User role not supported", userRole));
        }
    }

    public static String authenticateUsingApi(
            MockMvc mvc, ObjectMapper om, String login, String password) throws Exception {

        MvcResult result;
        result = mvc.perform(
                post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(fromPojoToString(om, new LoginRequestDto()
                                .setLogin(login)
                                .setPassword(password))
                        )
        )
                .andExpect(status().isOk())
                .andReturn();
        var loginResponseDto =
                fromMvcResultToBodyPojo(result, om, LoginResponseDto.class);
        return AuthHelper.JWT_BEARER_PREFIX + loginResponseDto.getJwt();
    }

    public static <T> String fromPojoToString(ObjectMapper om, T dto) throws Exception {
        return om.writeValueAsString(dto);
    }

    public static <T> T fromMvcResultToBodyPojo(
            MvcResult mvcResult, ObjectMapper om, Class<T> dtoClass) throws Exception {
        var response = mvcResult.getResponse();
        var bodyString = response.getContentAsString(StandardCharsets.UTF_8);
        return om.readValue(bodyString, dtoClass);
    }
}
