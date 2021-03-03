package com.polinakulyk.cashregister;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CashRegisterApplication {

    public static void main(String[] args) {
        log.info("Starting CashRegister application with args: '{}'",
                String.join(", ", args));
        SpringApplication.run(CashRegisterApplication.class, args);
    }
}
