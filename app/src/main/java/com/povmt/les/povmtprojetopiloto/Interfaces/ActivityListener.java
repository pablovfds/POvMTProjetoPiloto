package com.povmt.les.povmtprojetopiloto.Interfaces;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;

import java.util.List;

public interface ActivityListener {
    void receiverActivity(int statusCode, ActivityItem activityItem, String resp);
    void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp);
    void receiverActivity(int statusCode, String resp);
}
