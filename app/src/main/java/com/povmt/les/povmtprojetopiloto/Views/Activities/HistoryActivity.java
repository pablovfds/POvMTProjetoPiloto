package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class HistoryActivity extends AppCompatActivity implements InvestedTimeListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private List<InvestedTimeItem> investedTimeItems;
    private DatabaseReference mDatabase;

    private BarChart histChart;
    private List<BarEntry> histEntries;
    private List<String> histLabels;
    private LinearLayout histChartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        investedTimeItems = new ArrayList<>();

        FirebaseController.getInstance().retrieveAllInvestedTimeItems(mDatabase, investedTimeItems, HistoryActivity.this);

        histChart = (BarChart) findViewById(R.id.histchart);
        histEntries = new ArrayList<>();
        histLabels = new ArrayList<>();

        histChartLayout = (LinearLayout) findViewById(R.id.hist_graph_layout);
        histChartLayout.setVisibility(View.GONE);
    }

    private void plotHistoryChart() {
        int totalCurrentWeek = 0;
        int totalLastWeek = 0;
        int totalLastLastWeek = 0;

        for (InvestedTimeItem investedTimeItem : investedTimeItems) {
            if (investedTimeItem.isInvestedTimeWeek()) {
                totalCurrentWeek += investedTimeItem.getTime();
            } else if (investedTimeItem.isInvestedTimeLastWeek()) {
                totalLastWeek += investedTimeItem.getTime();
            } else if (investedTimeItem.isInvestedTimeLastLastWeek()) {
                totalLastLastWeek += investedTimeItem.getTime();
            }
        }

        histLabels.add("Atual");
        histEntries.add(new BarEntry(totalCurrentWeek, 0));
        histLabels.add("Passada");
        histEntries.add(new BarEntry(totalLastWeek, 1));
        histLabels.add("Retrasada");
        histEntries.add(new BarEntry(totalLastLastWeek, 2));

        BarDataSet barDataSet = new BarDataSet(histEntries, "Total de horas por semana");
        /*BarData barData = new BarData(histLabels, barDataSet);

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        histChart.setData(barData);
        histChart.animateY(3000);*/
    }

    @Override
    public void receiverTi(int statusCode, String resp) {

    }

    @Override
    public void receiverTi(int statusCode, List<InvestedTimeItem> investedTimeItems, String resp) {
        if (statusCode != 200) {
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            plotHistoryChart();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_general_report) {
            histChartLayout.setVisibility(View.VISIBLE);
        }
        return true;
    }
}
