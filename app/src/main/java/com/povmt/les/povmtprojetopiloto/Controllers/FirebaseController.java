package com.povmt.les.povmtprojetopiloto.Controllers;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.povmt.les.povmtprojetopiloto.Adapters.ActivityItemAdapter;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityCRUD;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseController implements ActivityCRUD {

    private static FirebaseController controller;

    public static FirebaseController getInstance(){
        if (controller == null){
            controller = new FirebaseController();
        }

        return controller;
    }

    @Override
    public void insertActivity(ActivityItem activityItem, DatabaseReference mDatabase, final ActivityListener listener) {
        DatabaseReference activitiesRef = mDatabase.child("activities");
        DatabaseReference newActivityRef = activitiesRef.push();

        newActivityRef.setValue(activityItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverActivity(databaseError.getCode(), false);
                } else {
                    listener.receiverActivity(200, true);
                }
            }
        });
    }

    @Override
    public void removeActitityById(String activityId, DatabaseReference mDatabase, ActivityListener listener) {

    }

    @Override
    public void updateActivity(String activityId, String name, DatabaseReference mDatabase, ActivityListener listener) {

    }

    @Override
    public void retrieveActivityById(String activityId, DatabaseReference mDatabase, ActivityListener listener) {
        //pegar lista de tempos investidos
    }

    @Override
    public void retrieveAllActivities(DatabaseReference mDatabase, final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {
        mDatabase.child("activities").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String uid) {
                String title = (String) dataSnapshot.child("title").getValue();
                String description = (String) dataSnapshot.child("description").getValue();

                ActivityItem activityItem = new ActivityItem(title, description);
                String createdAt = (String) dataSnapshot.child("createdAt").getValue();
                String updatedAt = (String) dataSnapshot.child("updatedAt").getValue();
                Log.d("Date", createdAt.toString());
                activityItem.setCreatedAt(createdAt);
                activityItem.setUpdatedAt(updatedAt);
                activityItem.setUid(uid);
                activityItem.setInvestedTimeList(new ArrayList<InvestedTime>());

                activityItems.add(activityItem);
                listener.receiverActivity(200, activityItems);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }
}
