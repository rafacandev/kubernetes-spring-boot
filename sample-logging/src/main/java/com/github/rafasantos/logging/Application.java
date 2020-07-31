package com.github.rafasantos.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
public class Application {
	public static void main(String[] args) throws IOException {
		String path = System.getenv("LOGGING_CONFIG");
		if (path != null && !path.isEmpty()) {
			System.out.println("Found environment variable LOGGING_CONFIG=" + path);
			String content = readFile(path, StandardCharsets.UTF_8);
			System.out.println("Loading logging config content");
			System.out.println(content);
		}
		SpringApplication.run(Application.class, args);
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
