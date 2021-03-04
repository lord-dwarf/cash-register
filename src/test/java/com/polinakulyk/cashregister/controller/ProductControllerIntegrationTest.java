package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.CashRegisterApplication;
import com.polinakulyk.cashregister.db.entity.Product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.authenticateUsingApi;
import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromMvcResultToBodyPojo;
import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromPojoToString;
import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.KILO;
import static com.polinakulyk.cashregister.security.api.AuthHelper.JWT_AUTH_HEADER;
import static com.polinakulyk.cashregister.security.dto.UserRole.MERCH;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalAmount;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalMoney;

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
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    public void createProduct_happy() throws Exception {

        var authHeader = authenticateUsingApi(mvc, om, MERCH);

        var result = mvc.perform(
                post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(fromPojoToString(om, new Product()
                                .setCode("TEST-CODE-1")
                                .setCategory("Test Category")
                                .setName("Test Name")
                                .setDetails("Test Details")
                                .setPrice(bigDecimalMoney("6.66"))
                                .setAmountAvailable(
                                        bigDecimalAmount("5.123", KILO))
                                .setAmountUnit(KILO)
                        ))
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var product = fromMvcResultToBodyPojo(result, om, Product.class);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotEmpty();
        assertThat(product.getCode()).isEqualTo("TEST-CODE-1");
        assertThat(product.getCategory()).isEqualTo("Test Category");
        assertThat(product.getName()).isEqualTo("Test Name");
        assertThat(product.getDetails()).isEqualTo("Test Details");
        assertThat(product.getPrice()).isEqualTo(bigDecimalMoney("6.66"));
        assertThat(product.getAmountAvailable()).isEqualTo(bigDecimalAmount("5.123", KILO));
        assertThat(product.getAmountUnit()).isEqualTo(KILO);
    }
}
