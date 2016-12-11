package com.povmt.les.povmtprojetopiloto.Controllers;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;

import java.util.List;

public class FirebaseController {

    private static FirebaseController controller;
    private static FirebaseAuth mAuth;
    private static final String USERS = "users";
    private static final String ACTIVITES = "activities";
    private static final String PHOTOS = "Photos";
    private static DatabaseReference mDatabase;
    private static StorageReference mStorage;

    public static FirebaseController getInstance(){
        if (controller == null && mAuth == null){
            controller = new FirebaseController();
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mStorage = FirebaseStorage.getInstance().getReference();
        }
        return controller;
    }

    public void insertActivity(final ActivityItem activityItem, final ActivityListener listener) {

        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        DatabaseReference activitiesRef = userRef.child(ACTIVITES);
        final DatabaseReference newActivityRef = activitiesRef.push();

        newActivityRef.setValue(activityItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.receiverActivity(databaseError.getCode() , activityItem, "Atividade não foi cadastrada");
                } else {
                    activityItem.setUid(newActivityRef.getKey());
                    listener.receiverActivity(200, activityItem, "Atividade cadastrada com sucesso");
                }
            }
        });
    }

    public void saveImageOfActivityItem(final ActivityItem item, byte[] imageByteArray){
        StorageReference imageRef = mStorage.child(PHOTOS).child(item.getUid());
        DatabaseReference userRef = mDatabase.child(USERS).child(getUid());
        final DatabaseReference activityRef = userRef.child(ACTIVITES)
                .child(item.getUid());

        UploadTask uploadTask = imageRef.putBytes(imageByteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                activityRef.child("imageUrl").setValue(item.getUid());
            }
        });
    }

    public void retrieveAllActivities(final List<ActivityItem> activityItems,
                                      final ActivityListener listener) {

        final DatabaseReference activitiesRef = mDatabase.child(USERS).child(getUid()).child(ACTIVITES);

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
                    String imageUrl = (String) postSnapshot.child("imageUrl").getValue();
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
                    activityItem.setImageUrl(imageUrl);

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
                listener.receiverActivity(databaseError.getCode(),
                        "Erro ao carregar a lista de atividades.");
            }
        });
    }

    public void insertTi(final ActivityItem activityItem, final InvestedTimeItem investedTime,
                         final InvestedTimeListener listener) {

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

    public void retrieveActivityImage(String imageId, final ActivityListener listener){
        StorageReference filePath = mStorage.child("Photos").child(imageId);

        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                listener.receiverImageUri(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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

    private String getUid(){
        return mAuth.getCurrentUser().getUid();
    }
}