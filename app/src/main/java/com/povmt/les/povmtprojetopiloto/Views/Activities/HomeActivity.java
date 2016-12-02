package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
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

public class HomeActivity extends AppCompatActivity implements ActivityListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.fab_add_activity_item) FloatingActionButton fab;

    @BindView(R.id.tv_total_time_invested) TextView ti_total;

    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;
    private ProgressDialog progressDialog;

    private List<ActivityItem> activityItemsWeek;
    private BarChart chart;
    private ArrayList<BarEntry> BARENTRY;
    private ArrayList<String> BarEntryLabels;
    private LinearLayout graphLayout;
    private int tempoTotal = 0;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerViewActivities;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient client;
    private GoogleApiClient mGoogleApiClient;

    private List<ActivityItem> activitiesTwoLastWeeks;
    private BarChart histChart;
    private List<BarEntry> entries;
    private List<String> labels;
    private LinearLayout chartLayoutHist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Atividades Recentes");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        activityItems = new ArrayList<>();

        setRecycleViews();

        activityItemsWeek = new ArrayList<>();
        activitiesTwoLastWeeks = new ArrayList<ActivityItem>();


        //Declaração das paradas pra gerar o gráfico
        chart = (BarChart) findViewById(R.id.chart1);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        //Declaração das paradas pra gerar o gráficoHist
        histChart = (BarChart) findViewById(R.id.histchart);
        entries = new ArrayList<BarEntry>();
        labels = new ArrayList<String>();

        //Aqui acontece a mágica da plotagem do gráfico
        sortListWeek();
        graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        graphLayout.setVisibility(View.GONE);

        chartLayoutHist = (LinearLayout) findViewById(R.id.graph_layout_hist);
        chartLayoutHist.setVisibility(View.GONE);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(HomeActivity.this, "Erro", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        infoUser();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_show_activities) {
            graphLayout.setVisibility(View.INVISIBLE);
            chartLayoutHist.setVisibility(View.INVISIBLE);
            recyclerViewActivities.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_show_graph) {
            graphLayout.setVisibility(View.VISIBLE);
            chartLayoutHist.setVisibility(View.INVISIBLE);
            recyclerViewActivities.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            ti_total.setText("Tempo investido : " + tempoTotal + " horas");

        }  else if (id == R.id.nav_general_report) {
            graphLayout.setVisibility(View.INVISIBLE);
            chartLayoutHist.setVisibility(View.VISIBLE);
            recyclerViewActivities.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            signOut();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Logout do aplicativo
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    /**
     * Carregar informaçoes do usuario logado
     */
    private void infoUser() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView nameUser = (TextView) header.findViewById(R.id.nome);
        TextView emailUser = (TextView) header.findViewById(R.id.email);

        nameUser.setText(mAuth.getCurrentUser().getDisplayName());
        emailUser.setText(mAuth.getCurrentUser().getEmail());
    }

    private void setRecycleViews() {
        recyclerViewActivities = (RecyclerView) findViewById(R.id.recycleview_activities);
        adapter = new ActivityItemAdapter(this, activityItems);
        recyclerViewActivities.setHasFixedSize(true);

        progressDialog.show();
        FirebaseController.getInstance().retrieveAllActivities(mDatabase, activityItems, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewActivities.setLayoutManager(llm);
        recyclerViewActivities.setAdapter(adapter);
    }

    @OnClick(R.id.fab_add_activity_item)
    public void addNewActivityItem() {
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
        if (statusCode != 200) {
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            adapter.update(activityItems);
            itensOfWeekAndGraph();
            plotBarChart();
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {

    }

    /**
     * Esse código está protegido pelas Leis de Deus, pq só ele sabe como isso ta funcionando apenas desse jeito.
     * Ass: Lúcio
     */
    private void itensOfWeekAndGraph() {

        int cont = 0;

        tempoTotal = 0;

        for (ActivityItem activityItem : activityItems) {
            if (activityItem.isActivityWeek()) {
                ActivityItem item = listContainsActivity(activityItemsWeek, activityItem.getUid());

                if (item == null) {
                    activityItemsWeek.add(activityItem);

                } else {
                    activityItemsWeek.remove(item);
                    activityItemsWeek.add(activityItem);
                }

                BarEntryLabels.add(activityItem.getTitle());
                BARENTRY.add(new BarEntry(activityItem.getTotalInvestedTime(), cont));
                tempoTotal += activityItem.getTotalInvestedTime();
            }

            cont++;
        }


        BarDataSet bardataset = new BarDataSet(BARENTRY, "Atividades");

        BarData BARDATA = new BarData(BarEntryLabels, bardataset);

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        chart.setData(BARDATA);

        chart.animateY(3000);
    }

    private void plotBarChart() {
        int counter = 0;
        for (ActivityItem activityitem : activitiesTwoLastWeeks) {
            if (activityitem.isActivityTwoLastWeeks()) {
                activitiesTwoLastWeeks.add(activityitem);

                labels.add(activityitem.getTitle());
                entries.add(new BarEntry(activityitem.getTotalInvestedTime(), counter));
            }
            counter++;
        }

        BarDataSet dataset = new BarDataSet(entries, "Atividades");
        BarData barData = new BarData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        histChart.setData(barData);
        histChart.animateY(3000);

    }

    private void sortListWeek() {
        Collections.sort(activityItemsWeek);
    }

    private ActivityItem listContainsActivity(List<ActivityItem> activityItems, String activityId) {
        for (int i = 0; i < activityItems.size(); i++) {
            ActivityItem item = activityItems.get(i);
            if (item.getUid().equals(activityId)) {
                return item;
            }
        }
        return null;
    }
}
