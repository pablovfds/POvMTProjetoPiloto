package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.povmt.les.povmtprojetopiloto.Service.AlarmReceiver;

import java.util.Calendar;

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

    Calendar calendar;
    private PendingIntent pendingIntent;

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

        FirebaseController.getInstance().getReminderTimeOfUser(mDatabase, this);



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

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, alarmIntent, 0);
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
        Log.e("RECEIVE TI", "ti no dia anterior? " + resp + " enable? " + switchRTEnable);
        if(resp == false && switchRTEnable){
            startAlarm();
        }else {
            cancelAlarm();
        }

       // Toast.makeText(this, "Houve cadastro no dia anterior? " + resp, Toast.LENGTH_SHORT).show();
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

    public void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, this.mHour);
        calendar.set(Calendar.MINUTE, this.mMinute);

        /* Repeating on every 24 hours interval */

         manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                 AlarmManager.INTERVAL_DAY, pendingIntent);
         Toast.makeText(this, "Alarm started!", Toast.LENGTH_SHORT).show();

    }

    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }


}
