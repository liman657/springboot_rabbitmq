package com.learn.springbootrabbitmqlearn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan(basePackages = "com.learn.springbootrabbitmqlearn.mapper")
@ImportResource(locations = {"classpath:spring/spring-jdbc.xml"})
@EnableCaching
public class SpringbootRabbitmqLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRabbitmqLearnApplication.class, args);
    }

}
