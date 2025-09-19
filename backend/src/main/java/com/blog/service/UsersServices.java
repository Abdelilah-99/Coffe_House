package com.blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.blog.dto.UsersRespons;
import com.blog.entity.*;
import com.blog.repository.UserRepository;

@Service
public class UsersServices {
    @Autowired
    private UserRepository userRepository;

    public List<UsersRespons> findAll() {
        List<User> users = userRepository.findAll();
        List<UsersRespons> userDTOs = new ArrayList<>();
        for (User user : users) {
            UsersRespons dto = convertToDto(user);
            userDTOs.add(dto);
        }
        return userDTOs;
    }

    private UsersRespons convertToDto(User user) {
        return new UsersRespons(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(),
                user.getEmail(), user.getRole());
    }

    public UsersRespons getCurrentUser() throws Exception {
        System.err.println("here");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication = " + authentication);
        System.out.println("Principal = " + authentication.getPrincipal());
        System.out.println("Authorities = " + authentication.getAuthorities());

        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String username = (String) authentication.getPrincipal();
            // System.out.printf("username in getcrr: \n", username);
            User user = userRepository.findByUserName(username).orElseThrow();
            return new UsersRespons(user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getRole());
        }
        throw new Exception("User not authenticated");
    }
}
