package com.povmt.les.povmtprojetopiloto.Models;

public class InvestedTimeItem {
    private double time;
    private String createdAt;

    public InvestedTimeItem() {
    }

    public InvestedTimeItem(double time, String createdAt) {
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
