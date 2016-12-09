package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.UserInfoListener;
import com.povmt.les.povmtprojetopiloto.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements InvestedTimeListener,
        UserInfoListener {

    @BindView(R.id.tv_reminder_time) TextView tv_reminder_time;

    @BindView(R.id.switchReminder) Switch switchReminderTime;

    @BindView(R.id.view_reminder_time) RelativeLayout rl_view;

    private int mHour, mMinute;
    private DatabaseReference mDatabase;
    private boolean switchRTEnable = true;

    // TODO: 08/12/2016 Adicionar o alarme aqui ou você que sabe


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setTitle("Configurações");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseController.getInstance().getRemiderTimeOfUser(mDatabase, this);

        switchReminderTime.setChecked(switchRTEnable);

        switchReminderTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                switchRTEnable = bChecked;
                if (bChecked) {
                    rl_view.setVisibility(View.VISIBLE);
                } else {
                    rl_view.setVisibility(View.INVISIBLE);
                }

                FirebaseController.getInstance().updateReminderTimeOfUser(mDatabase,
                        switchRTEnable, mHour, mMinute, SettingsActivity.this);
            }
        });
    }

    @OnClick(R.id.view_reminder_time)
    public void changeRemindTime(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        tv_reminder_time.setText(hourOfDay + " : " + minute);
                        mHour = hourOfDay;
                        mMinute = minute;

                        FirebaseController.getInstance().updateReminderTimeOfUser(mDatabase,
                                switchRTEnable, hourOfDay, minute, SettingsActivity.this);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        return false;
    }

    @Override
    public void receiverTi(int statusCode, String resp) {

    }

    @Override
    public void receiverTi(int statusCode, boolean resp) {
        //Aqui é recebido o valor da checagem se houve ou não atualizações no dia anterior
        // Se houve cadastro ele retorna TRUE, c.c. ele retorna FALSE

        Toast.makeText(this, "Houve cadastro no dia anterior? " + resp, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receiverUser(int statusCode, String resp) {
        if (statusCode != 200){
            Toast.makeText(this, "Erro ao tentar se conectar ao servidor", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void receiverUser(int statusCode, int mHour, int mMinute, boolean enableRT) {
        if (statusCode != 200){
            Toast.makeText(this, "Erro ao tentar se conectar ao servidor", Toast.LENGTH_SHORT)
                    .show();
        } else {
            this.switchRTEnable = enableRT;
            this.mHour = mHour;
            this.mMinute = mMinute;
            tv_reminder_time.setText(mHour + ":" + mMinute);
            FirebaseController.getInstance().checkUpdateInTimeInvested(mDatabase, this);
        }
    }
}
