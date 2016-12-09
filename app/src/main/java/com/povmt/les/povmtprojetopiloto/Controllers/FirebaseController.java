package com.povmt.les.povmtprojetopiloto.Controllers;

import com.google.firebase.auth.FirebaseAuth;
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
    private static FirebaseAuth mAuth;
    private static final String USERS = "users";
    private static final String ACTIVITIES = "activities";
    private static final String INVESTED_TIME_LIST = "investedTimeList";

    public static FirebaseController getInstance() {
        if (controller == null && mAuth == null) {
            controller = new FirebaseController();
            mAuth = FirebaseAuth.getInstance();
        }
        return controller;
    }

    public void insertActivity(ActivityItem activityItem, DatabaseReference mDatabase, final ActivityListener listener) {

        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference activitiesRef = userRef.child(ACTIVITIES);
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

        DatabaseReference activitiesRef = mDatabase.child(USERS).child(getUid()).child(ACTIVITIES);

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String activityID = postSnapshot.getKey();

                    ActivityItem auxItem = listContainsActivity(activityItems, activityID);

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

    public void retrieveAllInvestedTimeItems(DatabaseReference mDatabase, final List<InvestedTimeItem> investedTimeItems,
                                         final InvestedTimeListener listener) {

        DatabaseReference activitiesRef = mDatabase.child(USERS).child(getUid()).child(ACTIVITIES);

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot snapshot : postSnapshot.child(INVESTED_TIME_LIST).getChildren()) {

                        String investedTimeItemId = snapshot.getKey();

                        InvestedTimeItem auxItem = listContainsInvestedTimeItem(investedTimeItems, investedTimeItemId);

                        String createdAt = (String) snapshot.child("createdAt").getValue();
                        Object object = (Object) snapshot.child("time").getValue();

                        int time;

                        if (object != null) {
                            if (object instanceof Long) {
                                time = ((Long) object).intValue();
                            } else {
                                time = ((Double) object).intValue();
                            }
                        } else {
                            time = 0;
                        }

                        InvestedTimeItem investedTimeItem = new InvestedTimeItem(time, createdAt);

                        investedTimeItem.setUid(investedTimeItemId);

                        if (auxItem != null) {
                            investedTimeItems.remove(auxItem);
                        }
                        investedTimeItems.add(investedTimeItem);
                    }
                }
                listener.receiverTi(200, investedTimeItems, "Sucesso ao carregar TIs");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverTi(databaseError.getCode(), "Erro ao carregar TIs");
            }
        });

    }

    public void insertTi(final ActivityItem activityItem, final InvestedTimeItem investedTime,
                         DatabaseReference mDatabase, final InvestedTimeListener listener) {

        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        final DatabaseReference activitiesRef = userRef.child("activities").child(activityItem.getUid());
        DatabaseReference investedTimeRef = activitiesRef.child("investedTimeList");
        DatabaseReference newInvestedTimeRef = investedTimeRef.push();

        newInvestedTimeRef.setValue(investedTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverTi(databaseError.getCode(), "Falha ao cadastrar TI");
                } else {
                    DatabaseReference activityItemUpdateAtRef = activitiesRef.child("updatedAt");
                    DatabaseReference activityItemTotalTitRef = activitiesRef.child("totalInvestedTime");
                    activityItemUpdateAtRef.setValue(activityItem.getUpdatedAt());
                    activityItemTotalTitRef.setValue(activityItem.getTotalInvestedTime());
                    listener.receiverTi(200, "TI cadastrada com sucesso");

                }
            }
        });
    }

    private ActivityItem listContainsActivity(List<ActivityItem> activityItems, String activityId) {
        for (int i = 0; i < activityItems.size(); i++) {
            ActivityItem item = activityItems.get(i);
            if (item.getUid().equals(activityId)) {
                return item;
            }
        }
        return null;
    }

    private InvestedTimeItem listContainsInvestedTimeItem(List<InvestedTimeItem> investedTimeItems,
                                                          String investedTimeItemId) {
        for (int i = 0; i < investedTimeItems.size(); i++) {
            InvestedTimeItem item = investedTimeItems.get(i);
            if (item.getUid().equals(investedTimeItemId)) {
                return item;
            }
        }
        return null;
    }

    private String getUid() {
        return mAuth.getCurrentUser().getUid();
    }
}