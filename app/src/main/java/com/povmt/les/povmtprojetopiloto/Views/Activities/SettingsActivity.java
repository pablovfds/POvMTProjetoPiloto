package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements InvestedTimeListener {

    @BindView(R.id.tv_reminder_time) TextView tv_reminder_time;

    @BindView(R.id.switchReminder) Switch switchReminderTime;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Configurações");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseController.getInstance().checkIfTiIsRegisterInLastDay(mDatabase, this);
    }

    @OnClick(R.id.view_reminder_time)
    public void changeRemindTime(){

    }

    @Override
    public void receiverTi(int statusCode, String resp) {

    }
}
