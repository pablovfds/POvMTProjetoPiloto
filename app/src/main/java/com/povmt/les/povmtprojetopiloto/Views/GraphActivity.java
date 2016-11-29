package com.povmt.les.povmtprojetopiloto.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<ActivityItem> activityItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        activityItems = new ArrayList<>();

        lala();

    }
    private void lala(){

        final DatabaseReference atividades = mDatabase.child("activities");
        atividades.addChildEventListener(new ChildEventListener() {

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
        System.out.println(activityItems.get(0).getTitle());


    }

}
