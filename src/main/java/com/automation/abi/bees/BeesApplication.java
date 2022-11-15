package com.automation.abi.bees;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeesApplication {

	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(BeesApplication.class, args);
	}

}



