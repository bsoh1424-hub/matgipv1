package com.example.matgipv1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class Matgipv1Application {

    public static void main(String[] args) {
        SpringApplication.run(Matgipv1Application.class, args);
    }

}
