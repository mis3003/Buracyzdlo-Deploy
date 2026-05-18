package org.example.buraczydlobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.buraczydlobackend.repository")
@EntityScan(basePackages = "org.example.buraczydlobackend.model")
public class BuraczydloBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuraczydloBackendApplication.class, args);
    }

}
