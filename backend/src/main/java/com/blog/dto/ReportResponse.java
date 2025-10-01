package com.blog.dto;

public class ReportResponse {
    private String message;

    public ReportResponse() {
    }

    public ReportResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
