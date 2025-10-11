package com.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

public class RegisterRequest {
    @NotNull(message = "First name is required")
    @Size(min = 3, max = 15, message = "First name must be at least 3 characters long")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 3, max = 15, message = "Last name must be at least 3 characters long")
    private String lastName;

    @NotNull(message = "User name is required")
    @Size(min = 3, max = 15, message = "User name must be at least 3 characters long")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String role = "ROLE_USER";

    private MultipartFile profileImage;

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    // public void setRole(String role) {
    // this.role = role;
    // }
}
