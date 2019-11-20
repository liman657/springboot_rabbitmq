package com.learn.springbootrabbitmqblog;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SpringbootRabbitmqBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRabbitmqBlogApplication.class, args);
    }

}
