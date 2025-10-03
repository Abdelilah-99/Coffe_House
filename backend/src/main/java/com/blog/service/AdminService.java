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
import java.util.*;

@Service
public class AdminService {
    private UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UsersAdmineResponse> getUsers() {
        List<User> users = userRepository.findAll();
        List<UsersAdmineResponse> usersToDto = cnvToDto(users);
        return usersToDto;
    }

    private List<UsersAdmineResponse> cnvToDto(List<User> users) {
        List<UsersAdmineResponse> usersDto = new ArrayList<>();
        for (User user : users) {
            UsersAdmineResponse usersAdmineResponse = new UsersAdmineResponse();
            usersAdmineResponse.setEmail(user.getEmail());
            usersAdmineResponse.setFirstName(user.getFirstName());
            usersAdmineResponse.setLastName(user.getLastName());
            usersAdmineResponse.setUsername(user.getUserName());
            usersAdmineResponse.setUuid(user.getUuid());
            usersAdmineResponse.setPost(user.getPosts());
            usersDto.add(usersAdmineResponse);
        }
        return usersDto;
    }
}
