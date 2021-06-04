package com.setge.dddpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DddPostApplication {

  public static void main(String[] args) {
    SpringApplication.run(DddPostApplication.class, args);
  }

}
