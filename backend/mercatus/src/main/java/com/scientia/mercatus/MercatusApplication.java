package com.scientia.mercatus;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MercatusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MercatusApplication.class, args);
	}

    @Autowired
    private Environment env;

    @PostConstruct
    public void checkEnv() {
        String secret = env.getProperty("secret.key");
        System.out.println(">>> Spring sees secret.key: " + secret);
    }

}
