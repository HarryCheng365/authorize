package com.example.authorize;

import com.example.authorize.weixin.support.ApplicationStartup;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"com.example.authorize"})
@MapperScan(basePackages = "com.example.authorize.weixin.dao")
public class AuthorizeApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AuthorizeApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);

    }

}
