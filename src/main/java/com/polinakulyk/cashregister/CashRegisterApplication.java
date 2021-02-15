package com.polinakulyk.cashregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CashRegisterApplication {
	private static final Logger LOG = LoggerFactory.getLogger(CashRegisterApplication.class);

	public static void main(String[] args) {
		LOG.info("Before Starting application");
		SpringApplication.run(CashRegisterApplication.class, args);
		LOG.debug("Starting my application in debug with {} args", args.length);
		LOG.info("Starting my application with {} args.", args.length);
	}

}
