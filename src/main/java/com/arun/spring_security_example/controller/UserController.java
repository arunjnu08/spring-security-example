package com.arun.spring_security_example.controller;

import com.arun.spring_security_example.model.Users;
import com.arun.spring_security_example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//Authentication will be required for StudentController, but it will not be required for this controller
// For this I have added these lines in the securityFilterChain method of SecurityConfig.java
// .requestMatchers("register", "login")   // here we have mentioned register and login endpoints
// .permitAll()                          // auth will not be required for above register and login endpoints

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return service.verify(user);
    }
}
