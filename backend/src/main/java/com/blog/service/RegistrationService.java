package com.blog.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.blog.dto.RegisterRequest;
import com.blog.dto.RegisterResponse;
import com.blog.entity.User;
import com.blog.exceptions.*;
import com.blog.repository.UserRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;
    private final com.blog.security.InputSanitizationService inputSanitizationService;

    RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PostService postService,
            com.blog.security.InputSanitizationService inputSanitizationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.inputSanitizationService = inputSanitizationService;
    }

    public RegisterResponse register(RegisterRequest req, MultipartFile profileImage) {
        try {
            String sanitizedFirstName = inputSanitizationService.sanitizeFirstName(req.getFirstName());
            String sanitizedLastName = inputSanitizationService.sanitizeLastName(req.getLastName());
            String sanitizedEmail = inputSanitizationService.sanitizeEmail(req.getEmail());
            String sanitizedUsername = inputSanitizationService.sanitizeUsername(req.getUsername());
            String sanitizedPassword = inputSanitizationService.sanitizePassword(req.getPassword());

            if (userRepository.findByEmail(sanitizedEmail).isPresent()) {
                throw new UserAlreadyExistException(String.format("Email already exists: %s", sanitizedEmail));
            }
            if (userRepository.findByUserName(sanitizedUsername).isPresent()) {
                throw new UserAlreadyExistException(String.format("Username already exists: %s", sanitizedUsername));
            }
            String profilePath = "uploads/posts/profile.png";
            if (profileImage != null && !profileImage.isEmpty()) {
                String mimeType = profileImage.getContentType();
                if (mimeType == null ||
                        !(mimeType.startsWith("image/"))) {
                    throw new InvalidFormatException(null, "Invalide Format Type", mimeType, getClass());
                } else {
                    profilePath = postService.saveMedia(profileImage);
                }
            }
            String hashedPassword = passwordEncoder.encode(sanitizedPassword);
            User newUser = new User();
            newUser.setEmail(sanitizedEmail);
            newUser.setFirstName(sanitizedFirstName);
            newUser.setLastName(sanitizedLastName);
            newUser.setUserName(sanitizedUsername);
            newUser.setRole("ROLE_USER");
            newUser.setPassword(hashedPassword);
            newUser.setProfileImagePath(profilePath);
            newUser.setStatus("ACTIVE");
            newUser.setCreatedAt(System.currentTimeMillis());
            userRepository.save(newUser);
            return new RegisterResponse("User registered successfully", "ROLE_USER  ", sanitizedUsername);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (UserAlreadyExistException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrSavingException(e.getMessage());
        }
    }
}
