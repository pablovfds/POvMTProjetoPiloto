package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Adapters.ActivityItemAdapter;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewActivityDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements ActivityListener {

    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;
    private ProgressDialog progressDialog;

    private List<ActivityItem> activityItemsWeek;
    private BarChart chart ;
    private ArrayList<BarEntry> BARENTRY ;
    private ArrayList<String> BarEntryLabels ;
    private LinearLayout graphLayout;
    private int tempoTotal = 0;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerViewActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Ativiades Recentes");

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("activities");
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        activityItems = new ArrayList<>();

        setRecycleViews();

        activityItemsWeek = new ArrayList<>();

        //Declaração das paradas pra gerar o gráfico
        chart = (BarChart) findViewById(R.id.chart1);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        //Aqui acontece a mágica da plotagem do gráfico
        sortListWeek();
        graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        graphLayout.setVisibility(View.INVISIBLE);
    }

    private void setRecycleViews(){
        recyclerViewActivities = (RecyclerView) findViewById(R.id.recycleview_activities);
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
            itensOfWeekAndGraph();
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {

    }

    /**
     * Esse código está protegido pelas Leis de Deus, pq só ele sabe como isso ta funcionando apenas desse jeito.
     * Ass: Lúcio
     */
    private void itensOfWeekAndGraph(){

        int cont = 0; // esse contador aqui é a posição do elemento na lista, não vai ser alterado depois
        int tempo = 10; // esse tempo aqui é arbitrário, só para poder a barra ter um tamanho e aparecer
        // Quando Pablo trouxer do Firebase o tempo investido tiramos isso.
        // O método getSumOfTimeInvested() está retornando 0 por enquanto
        tempoTotal = 0;

        for (ActivityItem activityItem : activityItems) {
            if(activityItem.isActivityWeek()){
                ActivityItem item = listContainsActivity(activityItemsWeek, activityItem.getUid());

                if (item == null){
                    activityItemsWeek.add(activityItem);

                } else {
                    activityItemsWeek.remove(item);
                    activityItemsWeek.add(activityItem);
                }

                BarEntryLabels.add(activityItem.getTitle());
                BARENTRY.add(new BarEntry(activityItem.getTotalInvestedTime(), cont));
                tempoTotal += activityItem.getTotalInvestedTime();
            }

            cont ++;
        }


        BarDataSet bardataset = new BarDataSet(BARENTRY, "Atividades");

        BarData BARDATA = new BarData(BarEntryLabels, bardataset);

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        chart.setData(BARDATA);

        chart.animateY(3000);
    }

    private void sortListWeek(){
        Collections.sort(activityItemsWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_activity_item);
        TextView ti_total = (TextView) findViewById(R.id.tv_total_time_invested);

        switch (item.getItemId()) {
            case R.id.action_show_graph:
                graphLayout.setVisibility(View.VISIBLE);
                recyclerViewActivities.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
                ti_total.setText("Total de tempo investido: " + tempoTotal);
                break;
            case R.id.action_show_activities:
                graphLayout.setVisibility(View.INVISIBLE);
                recyclerViewActivities.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
