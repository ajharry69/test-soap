package com.github.ajharry69.testsoap;

import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
