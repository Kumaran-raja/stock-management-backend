package com.stock.stockmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockmanagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockmanagementApplication.class, args);
	}

}
