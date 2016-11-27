package com.povmt.les.povmtprojetopiloto.Models;

public class InvestedTime {
    private double time;
    private String createdAt;
    private String activityItemId;

    public InvestedTime() {
    }

    public InvestedTime(double time, String createdAt) {
        this.time = time;
        this.createdAt = createdAt;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
