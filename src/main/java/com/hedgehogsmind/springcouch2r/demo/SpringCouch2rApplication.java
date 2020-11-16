package com.hedgehogsmind.springcouch2r.demo;

import com.hedgehogsmind.springcouch2r.demo.repositories.NoteRepository;
import com.hedgehogsmind.springcouch2r.util.Couch2rAnnotationUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;

@SpringBootApplication(scanBasePackages = "com.hedgehogsmind.springcouch2r")
public class SpringCouch2rApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCouch2rApplication.class, args);
	}

}
