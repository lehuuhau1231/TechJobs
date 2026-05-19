package com.lhh.techjobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class TechjobsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechjobsApplication.class, args);
	}

}
