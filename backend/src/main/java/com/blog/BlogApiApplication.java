package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
// @EntityScan(basePackages = "com.blog.entity")
public class BlogApiApplication {

	public static void main(String[] args) {
		var t = SpringApplication.run(BlogApiApplication.class, args);
		for (String s : t.getBeanDefinitionNames()) {
			System.out.printf("test: %s\n", s);
		}
	}
}
