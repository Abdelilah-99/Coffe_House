package com.blog.security;

import org.springframework.stereotype.Service;

@Service
public class InputSanitizationService {
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_CONTENT_LENGTH = 10000;
    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 15;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 15;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public String sanitizeTitle(String title) {
        if (title == null) {
            return "";
        }
        title = title.trim();
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Title exceeds maximum length of %d characters", MAX_TITLE_LENGTH));
        }
        return title;
    }

    public String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        content = content.trim();
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Content exceeds maximum length of %d characters", MAX_CONTENT_LENGTH));
        }
        return content;
    }

    public String sanitizeComment(String comment) {
        if (comment == null) {
            return "";
        }
        comment = comment.trim();
        if (comment.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Comment exceeds maximum length of %d characters", MAX_COMMENT_LENGTH));
        }
        return comment;
    }

    public String sanitizeUsername(String username) {
        if (username == null) {
            return "";
        }
        username = username.trim();

        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("username must be at least %d characters long", MIN_USERNAME_LENGTH));
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Username exceeds maximum length of %d characters", MAX_USERNAME_LENGTH));
        }
        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                    "Username can only contain letters, numbers, dots, underscores, and hyphens");
        }
        return username;
    }

    public String sanitizeText(String text) {
        return sanitizeText(text, MAX_CONTENT_LENGTH);
    }

    public String sanitizeText(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        text = text.trim();

        if (text.length() > maxLength) {
            throw new IllegalArgumentException(
                    String.format("Text exceeds maximum length of %d characters", maxLength));
        }

        return text;
    }

    public String sanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        email = email.trim().toLowerCase();

        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Email exceeds maximum length of %d characters", MAX_EMAIL_LENGTH));
        }

        if (!email.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException(
                    "Invalid email format. Please provide a valid email address");
        }

        if (email.contains(" ")) {
            throw new IllegalArgumentException("Email must not contain spaces");
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Email must contain exactly one @ symbol");
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.length() > 64) {
            throw new IllegalArgumentException("Email local part exceeds maximum length of 64 characters");
        }

        if (domainPart.length() > 255) {
            throw new IllegalArgumentException("Email domain exceeds maximum length of 255 characters");
        }

        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            throw new IllegalArgumentException("Email local part cannot start or end with a dot");
        }

        if (localPart.contains("..")) {
            throw new IllegalArgumentException("Email local part cannot contain consecutive dots");
        }

        return email;
    }

    public String sanitizeFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        firstName = firstName.trim();

        if (firstName.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("First name must be at least %d characters long", MIN_NAME_LENGTH));
        }

        if (firstName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("First name exceeds maximum length of %d characters", MAX_NAME_LENGTH));
        }

        if (!firstName.matches("^[a-zA-Z]+$")) {
            throw new IllegalArgumentException(
                    "First name can only contain letters");
        }

        return firstName;
    }

    public String sanitizeLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        lastName = lastName.trim();

        if (lastName.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Last name must be at least %d characters long", MIN_NAME_LENGTH));
        }

        if (lastName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Last name exceeds maximum length of %d characters", MAX_NAME_LENGTH));
        }

        if (!lastName.matches("^[a-zA-Z]+$")) {
            throw new IllegalArgumentException(
                    "Last name can only contain letters");
        }

        return lastName;
    }

    public String sanitizePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        password = password.trim();

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password must be at least %d characters long", MIN_PASSWORD_LENGTH)
            );
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password exceeds maximum length of %d characters", MAX_PASSWORD_LENGTH)
            );
        }

        if (password.contains(" ")) {
            throw new IllegalArgumentException("Password must not contain spaces");
        }

        return password;
    }

}
