package com.hedgehogsmind.springcouch2r.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hedgehogsmind.springcouch2r")
public class SpringCouch2rApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCouch2rApplication.class, args);
	}

}
