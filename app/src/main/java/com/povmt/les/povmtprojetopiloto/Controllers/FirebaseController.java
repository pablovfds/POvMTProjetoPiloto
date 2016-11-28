package com.povmt.les.povmtprojetopiloto.Controllers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;

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
        DatabaseReference newActivityRef = mDatabase.push();

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

    public void retrieveAllActivities(DatabaseReference activitiesRef, final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String activityID = postSnapshot.getKey();

                    ActivityItem auxItem= listContainsActivity(activityItems, activityID);

                    String title = (String) postSnapshot.child("title").getValue();
                    String description = (String) postSnapshot.child("description").getValue();
                    String createdAt = (String) postSnapshot.child("createdAt").getValue();
                    String updatedAt = (String) postSnapshot.child("updatedAt").getValue();
                    Object object = postSnapshot.child("totalInvestedTime").getValue();

                    int totalInvestedTime;

                    if (object != null) {

                        if (object instanceof Long) {
                            totalInvestedTime = ((Long) object).intValue();
                        } else {
                            totalInvestedTime = Integer.valueOf((String) object);
                        }
                    } else {
                        totalInvestedTime = 0;
                    }

                    ActivityItem activityItem = new ActivityItem(title, description);

                    activityItem.setCreatedAt(createdAt);
                    activityItem.setUpdatedAt(updatedAt);
                    activityItem.setUid(activityID);
                    activityItem.setTotalInvestedTime(totalInvestedTime);

                    if (auxItem == null) {
                        activityItems.add(activityItem);
                    } else {
                        activityItems.remove(auxItem);
                        activityItems.add(activityItem);
                    }
                }
                listener.receiverActivity(200, activityItems, "Sucesso ao carregar lista de atividades.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverActivity(databaseError.getCode(), "Erro ao carregar a lista de atividades.");
            }
        });
    }

    public void insertTi(final ActivityItem activityItem, final InvestedTimeItem investedTime,
                         DatabaseReference mDatabase, final InvestedTimeListener listener) {

        final DatabaseReference activitiesRef = mDatabase.child("activities").child(activityItem.getUid());
        DatabaseReference investedTimeRef = activitiesRef.child("investedTimeList");
        DatabaseReference newInvestedTimeRef = investedTimeRef.push();

        newInvestedTimeRef.setValue(investedTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverTi(databaseError.getCode(), "Falha ao cadastrar TI");
                } else {
                    DatabaseReference activityItemUpdateAtRef =  activitiesRef.child("updatedAt");
                    DatabaseReference activityItemTotalTitRef =  activitiesRef.child("totalInvestedTime");
                    activityItemUpdateAtRef.setValue(activityItem.getUpdatedAt());
                    activityItemTotalTitRef.setValue(activityItem.getTotalInvestedTime());
                    listener.receiverTi(200, "TI cadastrada com sucesso");
                }
            }
        });
    }

    private ActivityItem listContainsActivity(List<ActivityItem> activityItems, String activityId){
        for (int i=0; i < activityItems.size(); i++) {
            ActivityItem item = activityItems.get(i);
            if (item.getUid().equals(activityId)){
                return item;
            }
        }
        return null;
    }
}
