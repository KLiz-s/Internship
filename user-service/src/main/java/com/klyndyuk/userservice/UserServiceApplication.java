package com.klyndyuk.userservice;

import com.klyndyuk.userservice.service.impl.UserServiceImpl;
import com.klyndyuk.userservice.service.interfaces.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
