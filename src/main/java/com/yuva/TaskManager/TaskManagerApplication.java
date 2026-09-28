package com.yuva.TaskManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TaskManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerApplication.class, args);
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//		System.out.println(encoder.encode("password1"));
//		System.out.println(encoder.encode("password2"));
	}

}
