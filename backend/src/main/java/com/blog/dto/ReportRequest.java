package com.blog.dto;

public class ReportRequest {
    private String reason;

    public ReportRequest() {
    }

    public ReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
