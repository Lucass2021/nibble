package com.lucasdias.nibble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NibbleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NibbleApplication.class, args);
    }
}
