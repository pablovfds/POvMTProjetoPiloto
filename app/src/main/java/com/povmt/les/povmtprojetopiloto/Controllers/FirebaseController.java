package com.povmt.les.povmtprojetopiloto.Controllers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

    public void retrieveAllActivities(DatabaseReference mDatabase, final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {

        DatabaseReference activitiesRef = mDatabase.child("activities");

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String title = (String) postSnapshot.child("title").getValue();
                    String description = (String) postSnapshot.child("description").getValue();
                    String createdAt = (String) postSnapshot.child("createdAt").getValue();
                    String updatedAt = (String) postSnapshot.child("updatedAt").getValue();
                    String activityID = postSnapshot.getKey();

                    ActivityItem activityItem = new ActivityItem(title, description);

                    activityItem.setCreatedAt(createdAt);
                    activityItem.setUpdatedAt(updatedAt);
                    activityItem.setUid(activityID);

                    activityItems.add(activityItem);                }
                listener.receiverActivity(200, activityItems, "Sucesso ao carregar listad de atividades.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverActivity(databaseError.getCode(), "Erro ao carregar a lista de atividades.");
            }
        });
    }

    public void insertTi(final ActivityItem activityItem, final InvestedTime investedTime,
                         DatabaseReference mDatabase, final InvestedTimeListener listener) {
        final DatabaseReference activitiesRef = mDatabase.child("activities").child(activityItem.getUid());
        DatabaseReference investedTimeRef = activitiesRef.child("investedTime");
        DatabaseReference newInvestedTimeRef = investedTimeRef.push();

        newInvestedTimeRef.setValue(investedTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverTi(databaseError.getCode(), "Falha ao cadastrar TI");
                } else {
                    DatabaseReference activityItemUpdateAtRef =  activitiesRef.child("updatedAt");
                    Log.d("date", activityItem.getUpdatedAt());
                    activityItemUpdateAtRef.setValue(activityItem.getUpdatedAt());
                    listener.receiverTi(200, "TI cadastrada com sucesso");
                }
            }
        });
    }

}
