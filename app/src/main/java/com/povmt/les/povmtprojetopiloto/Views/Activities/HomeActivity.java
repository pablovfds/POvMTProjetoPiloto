package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        activityItems = new ArrayList<>();
        retrieveAllActivities();
    }

    @OnClick(R.id.fab_add_activity_item)
    public void addNewActivityItem(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_register_activity_item);
        dialog.setTitle("Adicionar nova atividade");

        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreate);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        final TextInputEditText inputTitle = (TextInputEditText) dialog.findViewById(R.id.input_name_activity_item);
        final TextInputEditText inputDescription = (TextInputEditText) dialog.findViewById(R.id.input_description_activity_item);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inserir validações
                String titleActivity = inputTitle.getText().toString();
                String descriptionActivity = inputDescription.getText().toString();

                ActivityItem activityItem = new ActivityItem(titleActivity, descriptionActivity);

                FirebaseController.getInstance().insertActivity(activityItem, mDatabase, HomeActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void retrieveAllActivities() {
        adapter = new ActivityItemAdapter(this, activityItems);
        recyclerViewActivities.setHasFixedSize(true);
        progressDialog.show();
        FirebaseController.getInstance().retrieveAllActivities(mDatabase,activityItems, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewActivities.setLayoutManager(llm);
        recyclerViewActivities.setAdapter(adapter);
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
        progressDialog.dismiss();
        if (statusCode != 200){
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        }
    }

}
