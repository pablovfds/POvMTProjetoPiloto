package com.povmt.les.povmtprojetopiloto.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;

import java.util.List;

public interface ActivityCRUD {
    void insertActivity(String name, DatabaseReference mDatabase, ActivityListener listener);

    void removeActitityById(String activityId, DatabaseReference mDatabase, ActivityListener listener);

    void updateActivity(String activityId, String name, DatabaseReference mDatabase, ActivityListener listener);

    void retrieveActivityById(String activityId, DatabaseReference mDatabase, ActivityListener listener);

    void retrieveAllActivities(DatabaseReference mDatabase, final List<ActivityItem> activityItems,
                               ActivityListener listener);
}
