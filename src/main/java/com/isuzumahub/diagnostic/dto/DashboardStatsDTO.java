package com.isuzumahub.diagnostic.dto;

public class DashboardStatsDTO {
    private long totalUsers;
    private long totalReportsUploaded;

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalReportsUploaded() {
        return totalReportsUploaded;
    }

    public void setTotalReportsUploaded(long totalReportsUploaded) {
        this.totalReportsUploaded = totalReportsUploaded;
    }
}