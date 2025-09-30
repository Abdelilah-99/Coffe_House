package com.blog.dto;

public class ReportProfileResponse {
    private String message;

    public ReportProfileResponse() {
    }

    public ReportProfileResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
