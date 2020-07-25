package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HelloMessage {
	@Value("${spring.application.name}")
	private String applicationName;
	@Value("${hello.public.message}")
	private String helloPublicMessage;
	@Value("${hello.secret.message}")
	private String helloSecretMessage;

	@PostConstruct
	public void postConstruct() throws InterruptedException {
		System.out.println("--- Configuration Values ---");
		System.out.println("spring.application.name: " + applicationName);
		System.out.println("hello.public.message: " + helloPublicMessage);
		System.out.println("hello.secret.message: " + helloSecretMessage);
		System.out.println("----------------------------");
	}
}
