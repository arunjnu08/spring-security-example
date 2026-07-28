package com.arun.spring_security_example.service;

import com.arun.spring_security_example.model.Users;
import com.arun.spring_security_example.model.UserPrincipal;
import com.arun.spring_security_example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repo.findByUsername(username);
        if(user ==null) {
            System.out.println("User not found for username - " + username);
            throw new UsernameNotFoundException("user not found");
        }

        return new UserPrincipal(user);
    }
}
