package com.povmt.les.povmtprojetopiloto.Models;

import java.util.Date;

public class InvestedTime {
    private int time;
    private Date createdAt;
    private ActivityItem activityItem;

    public InvestedTime(int time, Date createdAt, ActivityItem activityItem) {
        this.time = time;
        this.createdAt = new Date();
        this.activityItem = activityItem;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ActivityItem getActivityItem() {
        return activityItem;
    }

    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
    }
}
