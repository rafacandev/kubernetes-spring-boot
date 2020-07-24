package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
public class HelloController {

	@Value("${hello.message}")
	private String helloMessage;

	@Value("${spring.application.name}")
	private String applicationName;

	@GetMapping("/hello")
	@ResponseBody
	public String sayHello() {
		return String.format("spring.application.name: %s | hello.message: %s", applicationName, helloMessage);
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("--- Configuration Values ---");
		System.out.println("spring.application.name: " + applicationName);
		System.out.println("hello.message: " + helloMessage);
		System.out.println("----------------------------");
	}

}
