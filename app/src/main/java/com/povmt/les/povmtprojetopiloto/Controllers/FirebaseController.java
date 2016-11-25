package com.povmt.les.povmtprojetopiloto.Controllers;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;

import java.util.ArrayList;
import java.util.List;

public class FirebaseController {

    private static FirebaseController controller;

    public static FirebaseController getInstance(){
        if (controller == null){
            controller = new FirebaseController();
        }

        return controller;
    }

    public void insertActivity(ActivityItem activityItem, DatabaseReference mDatabase, final ActivityListener listener) {
        DatabaseReference activitiesRef = mDatabase.child("activities");
        DatabaseReference newActivityRef = activitiesRef.push();

        newActivityRef.setValue(activityItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverActivity(databaseError.getCode(), "Atividade não foi cadastrada");
                } else {
                    listener.receiverActivity(200, "Atividade cadastrada com sucesso");
                }
            }
        });
    }

    public void retrieveActivityById(String activityId, DatabaseReference mDatabase, ActivityListener listener) {
        //pegar lista de tempos investidos
    }


    public void retrieveAllActivities(DatabaseReference mDatabase, final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {

        DatabaseReference activitiesRef = mDatabase.child("activities");

        activitiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String uid) {
                String title = (String) dataSnapshot.child("title").getValue();
                String description = (String) dataSnapshot.child("description").getValue();

                ActivityItem activityItem = new ActivityItem(title, description);
                String createdAt = (String) dataSnapshot.child("createdAt").getValue();
                String updatedAt = (String) dataSnapshot.child("updatedAt").getValue();
                activityItem.setCreatedAt(createdAt);
                activityItem.setUpdatedAt(updatedAt);
                activityItem.setUid(uid);
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

    public void insertTi(ActivityItem activityItem, InvestedTime investedTime,
                         DatabaseReference mDatabase, final InvestedTimeListener listener) {
        DatabaseReference activitiesRef = mDatabase.child("activities");
        Log.d("uid", activityItem.getUid());
        DatabaseReference investedTimeRef = activitiesRef.child(activityItem.getUid()).child("investedTime");
        DatabaseReference newInvestedTimeRef = investedTimeRef.push();

        newInvestedTimeRef.setValue(investedTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverTi(databaseError.getCode(), "Falha ao cadastrar TI");
                } else {
                    listener.receiverTi(200, "Ti cadastrada com sucesso");
                }
            }
        });
    }

}