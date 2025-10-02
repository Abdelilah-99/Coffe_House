package com.blog.dto;

public class UsersAdmineResponse {
    private String uuid;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public UsersAdmineResponse() {
    }

    public UsersAdmineResponse(
            String uuid,
            String firstName,
            String lastName,
            String username,
            String email) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
