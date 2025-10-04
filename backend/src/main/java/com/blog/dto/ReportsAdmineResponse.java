package com.blog.dto;

public class ReportsAdmineResponse {
    private String uuid;
    private String postOrUserUuid;
    private String reporterUsername;
    private String reason;
    private long time;

    public ReportsAdmineResponse() {
    }

    public ReportsAdmineResponse(String uuid,
            String reporterUsername,
            String reason,
            long time,
            String postOrUserUuid) {
        this.uuid = uuid;
        this.reporterUsername = reporterUsername;
        this.reason = reason;
        this.time = time;
        this.postOrUserUuid = postOrUserUuid;
    }

    public String getPostOrUserUuid() {
        return this.postOrUserUuid;
    }

    public void setPostOrUserUuid(String postOrUserUuid) {
        this.postOrUserUuid = postOrUserUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
