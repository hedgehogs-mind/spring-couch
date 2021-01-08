package com.hedgehogsmind.springcouchrest.demo;

import com.hedgehogsmind.springcouchrest.annotations.EnableCouchRest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hedgehogsmind.springcouchrest")
@EnableCouchRest
public class SpringCouchRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCouchRestApplication.class, args);
	}

}
