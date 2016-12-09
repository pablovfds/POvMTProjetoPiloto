package com.povmt.les.povmtprojetopiloto.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@IgnoreExtraProperties
public class InvestedTimeItem {
    private double time;
    private String createdAt;
    private String uid;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isInvestedTimeWeek() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(createdAt));

            if (cal.get(Calendar.WEEK_OF_YEAR) == Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isInvestedTimeLastWeek() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(createdAt));
            Calendar current = Calendar.getInstance();
            int weekOfActivity = cal.get(Calendar.WEEK_OF_YEAR);

            if (weekOfActivity == current.get(Calendar.WEEK_OF_YEAR) - 1) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isInvestedTimeLastLastWeek() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(createdAt));
            Calendar current = Calendar.getInstance();
            int weekOfActivity = cal.get(Calendar.WEEK_OF_YEAR);

            if (weekOfActivity == current.get(Calendar.WEEK_OF_YEAR) - 2) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
