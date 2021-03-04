package com.polinakulyk.cashregister.controller;

import com.polinakulyk.cashregister.CashRegisterApplication;
import com.polinakulyk.cashregister.controller.dto.AddReceiptItemRequestDto;
import com.polinakulyk.cashregister.db.dto.ReceiptStatus;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.UserRole;
import com.polinakulyk.cashregister.service.ServiceHelper;
import com.polinakulyk.cashregister.util.CashRegisterUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.authenticateUsingApi;
import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromMvcResultToBodyPojo;
import static com.polinakulyk.cashregister.controller.ControllerIntegrationTestHelper.fromPojoToString;
import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.KILO;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.COMPLETED;
import static com.polinakulyk.cashregister.db.dto.ReceiptStatus.CREATED;
import static com.polinakulyk.cashregister.security.api.AuthHelper.JWT_AUTH_HEADER;
import static com.polinakulyk.cashregister.security.dto.UserRole.MERCH;
import static com.polinakulyk.cashregister.security.dto.UserRole.SR_TELLER;
import static com.polinakulyk.cashregister.security.dto.UserRole.TELLER;
import static com.polinakulyk.cashregister.service.ServiceHelper.calcCostByPriceAndAmount;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalAmount;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalMoney;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
public class ReceiptControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    public void createReceipt_happy() throws Exception {

        var authHeader = authenticateUsingApi(mvc, om, TELLER);

        var result = mvc.perform(
                post("/api/receipts")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var receipt = fromMvcResultToBodyPojo(result, om, Receipt.class);
        assertThat(receipt).isNotNull();
        assertThat(receipt.getId()).isNotEmpty();
        assertThat(receipt.getCreatedTime()).isNotNull();
        assertThat(receipt.getCheckoutTime()).isNull();
        assertThat(receipt.getStatus()).isEqualTo(CREATED);
        assertThat(receipt.getSumTotal()).isNotNull();
    }

    @Test
    public void addReceiptItem_happy() throws Exception {

        String authHeader = authenticateUsingApi(mvc, om, MERCH);
        MvcResult result = mvc.perform(
                post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(fromPojoToString(om, new Product()
                                .setCode("TEST-CODE-10")
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
                .andReturn();

        var product = fromMvcResultToBodyPojo(result, om, Product.class);

        authHeader = authenticateUsingApi(mvc, om, SR_TELLER);
        result = mvc.perform(
                post("/api/receipts")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andReturn();

        var receipt = fromMvcResultToBodyPojo(result, om, Receipt.class);

        result = mvc.perform(
                post(format("/api/receipts/%s/items", receipt.getId()))
                        .contentType(APPLICATION_JSON)
                        .content("{\"productId\":\"" + product.getId() +
                                 "\",\"receiptItemAmount\":\"0.421\"}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        var receiptWithItem = fromMvcResultToBodyPojo(result, om, Receipt.class);
        assertThat(receiptWithItem).isNotNull();

        var receiptItems = receiptWithItem.getReceiptItems();
        assertThat(receiptItems).isNotNull();
        assertThat(receiptItems).isNotEmpty();

        var receiptItem = receiptItems.stream().findFirst().orElseThrow();
        assertThat(receiptItem.getId()).isNotEmpty();
        assertThat(receiptItem.getProduct()).isNotNull();
        assertThat(receiptItem.getProduct().getId()).isEqualTo(product.getId());
        assertThat(receiptItem.getName()).isEqualTo(product.getName());
        assertThat(receiptItem.getPrice()).isEqualTo(product.getPrice());
        assertThat(receiptItem.getAmount()).isEqualTo(bigDecimalAmount("0.421", KILO));
        assertThat(receiptItem.getAmountUnit()).isEqualTo(KILO);

        assertThat(receiptWithItem.getSumTotal()).isEqualTo(
                calcCostByPriceAndAmount(receiptItem.getPrice(), receiptItem.getAmount()));
    }

    @Test
    public void completeReceipt_happy() throws Exception {

        String authHeader = authenticateUsingApi(mvc, om, MERCH);
        MvcResult result = mvc.perform(
                post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(fromPojoToString(om, new Product()
                                .setCode("TEST-CODE-11")
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
                .andReturn();

        var product = fromMvcResultToBodyPojo(result, om, Product.class);

        authHeader = authenticateUsingApi(mvc, om, SR_TELLER);
        result = mvc.perform(
                post("/api/receipts")
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andReturn();

        Receipt receipt = fromMvcResultToBodyPojo(result, om, Receipt.class);

        result = mvc.perform(
                post(format("/api/receipts/%s/items", receipt.getId()))
                        .contentType(APPLICATION_JSON)
                        .content("{\"productId\":\"" + product.getId() +
                                 "\",\"receiptItemAmount\":\"0.421\"}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andReturn();

        receipt = fromMvcResultToBodyPojo(result, om, Receipt.class);

        result = mvc.perform(
                patch(format("/api/receipts/%s/complete", receipt.getId()))
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .header(JWT_AUTH_HEADER, authHeader)
        )
                .andExpect(status().isOk())
                .andReturn();

        receipt = fromMvcResultToBodyPojo(result, om, Receipt.class);
        assertThat(receipt).isNotNull();
        assertThat(receipt.getStatus()).isEqualTo(COMPLETED);
        assertThat(receipt.getCheckoutTime()).isNotNull();
    }
}
