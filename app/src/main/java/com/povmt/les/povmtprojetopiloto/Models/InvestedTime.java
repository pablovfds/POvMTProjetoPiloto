package com.povmt.les.povmtprojetopiloto.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class InvestedTime {
    private int time;
    private String createdAt;
    private String activityItemId;
    private Calendar date;

    public InvestedTime() {
        this.date = Calendar.getInstance();
    }

    public InvestedTime(int time) {
        this.time = time;
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setCalendar(cal);
        this.createdAt = dateFormat.format(cal.getTime());
        this.date = Calendar.getInstance();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getActivityItemId() {
        return activityItemId;
    }

    public void setActivityItemId(String activityItemId) {
        this.activityItemId = activityItemId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
