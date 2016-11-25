package com.povmt.les.povmtprojetopiloto.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class ActivityItem implements Serializable {

    private List<InvestedTime> investedTimeList;
    private String updatedAt;
    private String createdAt;
    private String description;
    private String title;
    private String uid;

    public ActivityItem() {
    }

    public ActivityItem(String title, String description) {
        this.title = title;
        this.description = description;
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateFormat.setCalendar(cal);
        this.createdAt = dateFormat.format(cal.getTime());
        this.updatedAt = this.createdAt;
        this.investedTimeList = new ArrayList<>();
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

    public List<InvestedTime> getInvestedTimeList() {
        return investedTimeList;
    }

    public void setInvestedTimeList(List<InvestedTime> investedTimeList) {
        this.investedTimeList = investedTimeList;
    }

    @Exclude
    public void addNewInvestedTime(InvestedTime investedTime){
        this.investedTimeList.add(investedTime);
    }

    @Exclude
    public float getSumOfTimeInvested(){
        float sum = 0;

        for (InvestedTime investedTime: investedTimeList) {
            sum += investedTime.getTime();
        }
        return sum;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        return result;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}