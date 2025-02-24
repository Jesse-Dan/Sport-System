package com.backend.golvia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = "com.backend.golvia.usermgt.services")
public class  GolviaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GolviaApplication.class, args);
	}

}
