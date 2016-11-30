package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity implements ActivityListener {
    private List<ActivityItem> activities;
    private List<ActivityItem> activitiesTwoLastWeeks;
    private DatabaseReference mDatabase;

    private BarChart chart;
    private List<BarEntry> entries;
    private List<String> labels;
    private BarDataSet dataset;
    private LinearLayout chartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("activities");

        activities = new ArrayList<ActivityItem>();
        activitiesTwoLastWeeks = new ArrayList<ActivityItem>();

        FirebaseController.getInstance().retrieveAllActivities(mDatabase, activities, HistoricoActivity.this);

        chart = (BarChart) findViewById(R.id.chart1);
        entries = new ArrayList<BarEntry>();
        labels = new ArrayList<String>();

        chartLayout = (LinearLayout) findViewById(R.id.graph_layout);
        chartLayout.setVisibility(View.GONE);
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {
        //
    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {
        if (statusCode != 200) {
            Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
        } else {
            plotBarChart();
        }
    }

    private void plotBarChart() {
        int counter = 0;
        for (ActivityItem activityitem : activities) {
            if (activityitem.isActivityTwoLastWeeks()) {
                activitiesTwoLastWeeks.add(activityitem);

                labels.add(activityitem.getTitle());
                entries.add(new BarEntry(activityitem.getTotalInvestedTime(), counter));
            }
            counter++;
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {
        //
    }
}