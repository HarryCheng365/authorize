package com.example.authorize;

import com.example.authorize.weixin.support.ApplicationStartup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class AuthorizeApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AuthorizeApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);

    }

}
