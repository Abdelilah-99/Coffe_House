package com.blog.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.UserRepository;
import com.blog.service.PostService;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PostService postService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
    }

    public RegisterResponse register(RegisterRequest req, MultipartFile profileImage) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new UserAlreadyExistException(String.format("Email already Exists %s", req.getEmail()));
        }
        if (userRepository.findByUserName(req.getUsername()).isPresent()) {
            throw new UserAlreadyExistException(String.format("Username already Exists %s", req.getUsername()));
        }
        String profilePath = "uploads/posts/profile.png";
        if (profileImage != null) {
            System.out.println("image ?!?!?!");
            profilePath = postService.saveMedia(profileImage);
        }
        String hashedPassword = passwordEncoder.encode(req.getPassword());
        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setFirstName(req.getFirstName());
        newUser.setLastName(req.getLastName());
        newUser.setUserName(req.getUsername());
        newUser.setRole(req.getRole());
        newUser.setPassword(hashedPassword);
        newUser.setProfileImagePath(profilePath);
        // req.getEmail(),
        // req.getFirstName(),
        // req.getLastName(),
        // req.getUsername(),
        // req.getRole(),
        // hashedPassword,
        // profilePath
        try {
            userRepository.save(newUser);
            return new RegisterResponse("User registered successfully", req.getRole(), req.getUsername());
        } catch (Exception e) {
            throw new ErrSavingException(String.format("failed to save data %s", e));
        }
    }
}
