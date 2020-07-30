package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HelloMessage {
	@Value("${spring.application.name}")
	private String applicationName;
	@Value("${hello.message.environment.variable}")
	private String messageEnvironmentVariable;
	@Value("${hello.message.public}")
	private String messagePublic;
	@Value("${hello.message.secret}")
	private String messageSecret;

	@PostConstruct
	public void postConstruct() throws InterruptedException {
		System.out.println("--- Configuration Values ---");
		System.out.println("spring.application.name: " + applicationName);
		System.out.println("hello.message.environment.variable: " + messageEnvironmentVariable);
		System.out.println("hello.message.public: " + messagePublic);
		System.out.println("hello.message.secret: " + messageSecret);
		System.out.println("----------------------------");
	}
}
