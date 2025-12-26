package com.uixs;


import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan("com.uixs")
public class UixsBootApplication extends SpringBootServletInitializer {
	private static int startCount = 0;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		System.out.println("####### start");
		System.out.println("####### SpringBootServletInitializer.configure() called - Count: " + (++startCount));
        System.out.println("####### Thread: " + Thread.currentThread().getName());
        System.out.println("####### Time: " + new java.util.Date());
        
        // 스택 트레이스로 어디서 호출되는지 확인
        new Exception("Stack trace").printStackTrace();
        
	    return builder.sources(UixsBootApplication.class);
	}
	
	public static void main(String[] args) {
		System.out.println("####### main() method called");
		SpringApplication.run(UixsBootApplication.class, args);
	}
	
	@PostConstruct
    public void init() {
        System.out.println("####### Application initialized - " + new java.util.Date());
    }

}
