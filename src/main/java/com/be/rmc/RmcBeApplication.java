package com.be.rmc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RmcBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RmcBeApplication.class, args);
	}

}
