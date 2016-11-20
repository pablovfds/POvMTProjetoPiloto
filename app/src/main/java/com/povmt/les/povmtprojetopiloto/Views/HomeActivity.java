package com.povmt.les.povmtprojetopiloto.Views;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.MenuItem;
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
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class HomeActivity extends AppCompatActivity implements ActivityListener{

    @BindView(R.id.fabSpeedDial_add)
    FabSpeedDial fabSpeedDial;

    @BindView(R.id.recycleview_activities)
    RecyclerView recyclerViewActivities;

    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        activityItems = new ArrayList<>();
        retrieveAllActivities();

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity

                if (menuItem.getItemId() == R.id.action_add_ti){
                    Intent intent = new Intent(HomeActivity.this, RegisterInvestedTimeActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.action_add_activity){
                    registerNewActivity();
                }

                return false;
            }
        });
    }

    private void retrieveAllActivities() {

        recyclerView = (RecyclerView) findViewById(R.id.recycleview_activities);
        adapter = new ActivityItemAdapter(this, activityItems);
        recyclerView.setHasFixedSize(true);
        FirebaseController.getInstance().retrieveAllActivities(mDatabase,activityItems, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    private void registerNewActivity() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_register_activity_item);
        dialog.setTitle("Adicionar nova atividade");

        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreate);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        final TextInputEditText inputTitle = (TextInputEditText) dialog.findViewById(R.id.input_name_activity_item);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleActivity = inputTitle.getText().toString();                
                FirebaseController.getInstance().insertActivity(titleActivity, mDatabase, HomeActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems) {
        if (statusCode != 200){
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            adapter.update(activityItems);
            Log.d("ro", "assa");
        }
    }

    @Override
    public void receiverActivity(int statusCode, boolean resp) {
        if (statusCode != 200){
            Toast.makeText(this, "Atividade não foi cadastrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Atividade cadastrada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }
}
