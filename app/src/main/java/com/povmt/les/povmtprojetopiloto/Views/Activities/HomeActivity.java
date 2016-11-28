package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Adapters.ActivityItemAdapter;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewActivityDialogFragment;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewTiDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements ActivityListener {

    @BindView(R.id.recycleview_activities) RecyclerView recyclerViewActivities;

    private DatabaseReference mDatabase;
    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("activities");
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        activityItems = new ArrayList<>();

        adapter = new ActivityItemAdapter(this, activityItems);
        recyclerViewActivities.setHasFixedSize(true);
        progressDialog.show();
        FirebaseController.getInstance().retrieveAllActivities(mDatabase,activityItems, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewActivities.setLayoutManager(llm);
        recyclerViewActivities.setAdapter(adapter);
    }

    @OnClick(R.id.fab_add_activity_item)
    public void addNewActivityItem(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RegisterNewActivityDialogFragment registerActivityDialog = new RegisterNewActivityDialogFragment();
        registerActivityDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        registerActivityDialog.show(ft, "registerActivityDialog");
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {
        progressDialog.dismiss();
        if (statusCode != 200){
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            adapter.update(activityItems);
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {

    }

}
