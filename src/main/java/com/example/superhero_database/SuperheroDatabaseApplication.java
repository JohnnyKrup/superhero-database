package com.example.superhero_database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

@SpringBootApplication
public class SuperheroDatabaseApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SuperheroDatabaseApplication.class, args);
		ConfigurableEnvironment env = ctx.getEnvironment();

		/**
		System.out.println("JWT_SECRET_KEY: " + env.getProperty("JWT_SECRET_KEY"));
		System.out.println("SUPERHERO_API_KEY: " + env.getProperty("SUPERHERO_API_KEY"));
		*/

	}

}
