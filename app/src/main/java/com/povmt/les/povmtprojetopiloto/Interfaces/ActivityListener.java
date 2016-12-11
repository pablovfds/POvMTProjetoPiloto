package com.povmt.les.povmtprojetopiloto.Interfaces;

import android.net.Uri;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;

import java.util.List;

public interface ActivityListener {
    void receiverActivity(int statusCode, ActivityItem activityItem, String resp);
    void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp);
    void receiverImageUri(Uri uri);

    void receiverActivity(int code, String s);
}
