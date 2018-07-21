package com.stat.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = { "com.stat.config", "com.stat.controller", "com.stat.service", "com.stat.model" })

@SpringBootApplication
/*
 * Below annotation will enable the scheduling of
 * function("calculateTransactionStats") in Statistics service class, this
 * function is the core of this whole service, For more details please read the
 * comments for that function
 */
@EnableScheduling
public class StatisticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticsApplication.class, args);
	}
}
