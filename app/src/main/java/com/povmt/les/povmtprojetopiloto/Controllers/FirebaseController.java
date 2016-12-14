package com.povmt.les.povmtprojetopiloto.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.UserInfoListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirebaseController {

    private static FirebaseController controller;
    private static FirebaseAuth mAuth;
    private static final String USERS = "users";
    private static final String ACTIVITES = "activities";

    private static final int DEFAULT_HOUR_REMINDER_TIME = 14;
    private static final int DEFAULT_MINUTE_REMINDER_TIME = 10;

    public static FirebaseController getInstance(){
        if (controller == null && mAuth == null){
            controller = new FirebaseController();
            mAuth = FirebaseAuth.getInstance();
        }
        return controller;
    }

    public void insertActivity(ActivityItem activityItem, DatabaseReference mDatabase, final ActivityListener listener) {

        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference activitiesRef = userRef.child(ACTIVITES);
        DatabaseReference newActivityRef = activitiesRef.push();

        newActivityRef.setValue(activityItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverActivity(databaseError.getCode(), "Erro ao cadastrar atividade");
                } else {
                    listener.receiverActivity(200, "Atividade cadastrada com sucesso");
                }
            }
        });
    }

    public void retrieveAllActivities(DatabaseReference mDatabase, final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {

        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference activitiesRef = userRef.child(ACTIVITES);

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
                    DatabaseReference activityItemUpdateAtRef =  activitiesRef.child("updatedAt");
                    DatabaseReference activityItemTotalTitRef =  activitiesRef.child("totalInvestedTime");
                    activityItemUpdateAtRef.setValue(activityItem.getUpdatedAt());
                    activityItemTotalTitRef.setValue(activityItem.getTotalInvestedTime());
                    listener.receiverTi(200, "TI cadastrado com sucesso");
                }
            }
        });
    }

    public void checkUpdateInTimeInvested(DatabaseReference mDatabase, final InvestedTimeListener listener){
        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        final DatabaseReference activitiesRef = userRef.child("activities");

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String updatedAt = (String) postSnapshot.child("updatedAt").getValue();
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                    Date date = null;

                    try {
                        date = df.parse(updatedAt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calUpdateAt = Calendar.getInstance();
                    calUpdateAt.setTime(date);

                    Calendar calYesterdayDate = Calendar.getInstance();
                    calYesterdayDate.add(Calendar.DATE, -1);

                    boolean resp = calYesterdayDate.compareTo(calUpdateAt) <= 0;

                    listener.receiverTi(200, resp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverTi(400, false);
            }
        });
    }

    public void getReminderTimeOfUser(DatabaseReference mDatabase, final UserInfoListener listener) {
        final DatabaseReference userRef = mDatabase.child(USERS).child(getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot reminderTimeDS = dataSnapshot.child("reminder_time");

                int mHour = DEFAULT_HOUR_REMINDER_TIME, mMinute = DEFAULT_MINUTE_REMINDER_TIME;
                boolean enableRT = true;

                if (reminderTimeDS.getValue() == null){
                    DatabaseReference reminderTimeRef = userRef.child("reminder_time");
                    reminderTimeRef.child("Hour").setValue(mHour);
                    reminderTimeRef.child("Minute").setValue(mMinute);
                    reminderTimeRef.child("EnableReminderTime").setValue(true);
                } else {
                    Object objHour = reminderTimeDS.child("Hour").getValue();
                    Object objMinute = reminderTimeDS.child("Minute").getValue();
                    Object objEnableRT = reminderTimeDS.child("EnableReminderTime").getValue();

                    enableRT = objEnableRT == null || (boolean) objEnableRT;

                    if (objHour != null && objMinute != null) {
                        if (objHour instanceof Long && objMinute instanceof Long) {
                            mHour = ((Long) objHour).intValue();
                            mMinute = ((Long) objMinute).intValue();
                        } else {
                            mHour = (int) objHour;
                            mMinute = (int) objMinute;
                        }
                    } else {
                        mHour = 14;
                        mMinute = 10;
                    }
                }

                listener.receiverUser(200, mHour, mMinute, enableRT);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverUser(databaseError.getCode(), databaseError.getMessage());
            }
        });
    }

    public void updateReminderTimeOfUser(DatabaseReference mDatabase,boolean enableReminderTime
            ,int mHour, int mMinute, final UserInfoListener listener){

        final DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference reminderTimeRef = userRef.child("reminder_time");

        reminderTimeRef.child("Hour").setValue(mHour);
        reminderTimeRef.child("Minute").setValue(mMinute);
        reminderTimeRef.child("EnableReminderTime").setValue(enableReminderTime);

        listener.receiverUser(200, "Horario do lembrete atualizado com sucesso");
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

    private String getUid(){
        return mAuth.getCurrentUser().getUid();
    }
}