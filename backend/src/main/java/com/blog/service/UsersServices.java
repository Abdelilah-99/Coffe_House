package com.blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}
