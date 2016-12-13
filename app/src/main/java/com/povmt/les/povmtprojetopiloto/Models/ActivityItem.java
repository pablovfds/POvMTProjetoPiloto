package com.povmt.les.povmtprojetopiloto.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ActivityItem implements Serializable, Comparable<ActivityItem> {

    private String updatedAt;
    private String createdAt;
    private String description;
    private String title;
    private String uid;
    private int prioridade;
    private int totalInvestedTime;
    private int totalInvestedTimeWeek;

    public ActivityItem() {
    }

    public ActivityItem(String title, String description, int prioridade) {
        this.title = title;
        this.description = description;
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setCalendar(cal);
        this.createdAt = dateFormat.format(cal.getTime());
        this.updatedAt = this.createdAt;
        this.totalInvestedTime = 0;
        this.prioridade = prioridade;
        this.totalInvestedTimeWeek = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getTotalInvestedTime() {
        return totalInvestedTime;
    }

    public void setTotalInvestedTime(int totalInvestedTime) {
        this.totalInvestedTime = totalInvestedTime;
    }

    public int getTotalInvestedTimeWeek() {
        return totalInvestedTimeWeek;
    }

    public void setTotalInvestedTimeWeek(int totalInvestedTimeWeek) {
        this.totalInvestedTimeWeek = totalInvestedTimeWeek;
    }

    @Exclude
    public boolean isActivityWeek() {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(updatedAt));

            if (cal.get(Calendar.WEEK_OF_YEAR) == Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) {
                System.out.println("TRUE");
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Exclude
    public void addNewInvestedTime(InvestedTimeItem investedTimeItem) {
        totalInvestedTime += investedTimeItem.getTime();
        if (investedTimeItem.isInvestedTimeWeek()) {
            totalInvestedTimeWeek += investedTimeItem.getTime();
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("description", description);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);
        result.put("sumInvestedTime", totalInvestedTime);
        result.put("prioridade", prioridade);
        result.put("sumInvestedTimeWeek", totalInvestedTimeWeek);
        return result;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public int compareTo(ActivityItem otherActivityItem) {

        if (this.getTotalInvestedTime() < otherActivityItem.getTotalInvestedTime()) {
            return -1;
        } else if (this.getTotalInvestedTime() > otherActivityItem.getTotalInvestedTime()) {
            return 1;
        } else {
            return 0;
        }
    }
}
