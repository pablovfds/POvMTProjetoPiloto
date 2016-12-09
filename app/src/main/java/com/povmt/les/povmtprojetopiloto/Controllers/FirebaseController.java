package com.povmt.les.povmtprojetopiloto.Controllers;

import android.provider.ContactsContract;

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
                    listener.receiverActivity(databaseError.getCode(), "Atividade não foi cadastrada");
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
                    listener.receiverTi(200, "TI cadastrada com sucesso");
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
                    String otherDate = "21/02/1992";
                    Date date = null;
                    Date date2= null;
                    try {
                        date = df.parse(updatedAt);
                        date2 = df.parse(otherDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    Calendar calActual = Calendar.getInstance();
                    cal.setTime(date);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    calActual.setTime(date2);
                    dateFormat.setCalendar(calActual);
                    String dateActual = dateFormat.format(calActual.getTime());

                    listener.receiverTi(200, dateActual.equals(updatedAt));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverTi(400, false);
            }
        });
    }

    public void getRemiderTimeOfUser(DatabaseReference mDatabase, final UserInfoListener listener) {
        final DatabaseReference userRef = mDatabase.child(USERS).child(getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ref = dataSnapshot.child("reminder_time");

                int mHour, mMinute;
                if (ref.getValue() == null){
                    mHour = 0;
                    mMinute = 0;
                    DatabaseReference reminderTimeRef = userRef.child("reminder_time");
                    reminderTimeRef.child("Hour").setValue(mHour);
                    reminderTimeRef.child("Minute").setValue(mMinute);
                } else {
                    Object objHour = ref.child("Hour").getValue();
                    Object objMinute = ref.child("Minute").getValue();

                    if (objHour instanceof Long) {
                        mHour = ((Long) objHour).intValue();
                        mMinute = ((Long) objMinute).intValue();
                    } else {
                        mHour = (int) objHour;
                        mMinute = (int) objMinute;
                    }
                }

                listener.receiverUser(200, mHour, mMinute);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.receiverUser(400, databaseError.getMessage());
            }
        });
    }

    public void updateReminderTimeOfUser(DatabaseReference mDatabase,int mHour, int mMinute, final UserInfoListener listener){
        final DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference reminderTimeRef = userRef.child("reminder_time");

        reminderTimeRef.child("Hour").setValue(mHour);
        reminderTimeRef.child("Minute").setValue(mMinute);

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