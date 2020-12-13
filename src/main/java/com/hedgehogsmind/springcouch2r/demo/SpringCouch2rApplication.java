package com.hedgehogsmind.springcouch2r.demo;

import com.hedgehogsmind.springcouch2r.beans.EnableCouch2r;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hedgehogsmind.springcouch2r")
@EnableCouch2r
public class SpringCouch2rApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCouch2rApplication.class, args);
	}

}
