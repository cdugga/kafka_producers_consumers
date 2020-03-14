package com.cdugga.opendata.lambda.stationdatalambdajava;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StationDataLambdaJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(StationDataLambdaJavaApplication.class, args);
	}

	@Bean
    public Function<String, String> reverseString() {
        return value -> new StringBuilder(value).reverse().toString();
    }
}
