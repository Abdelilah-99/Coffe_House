package com.blog.service;

import com.blog.entity.User;
import com.blog.dto.UsersAdmineResponse;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.UserRepository;
import com.blog.config.JwtUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    // private UserRepository userRepository;

    // public AdminService(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }

    // public UsersAdmineResponse getUsers() {

    // }
}
