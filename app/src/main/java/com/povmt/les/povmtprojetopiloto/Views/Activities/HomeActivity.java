package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;
import com.povmt.les.povmtprojetopiloto.Models.ChartAxisFormatters.MyXAxisValueFormatter;
import com.povmt.les.povmtprojetopiloto.Models.ChartAxisFormatters.MyYAxisValueFormatter;
import com.povmt.les.povmtprojetopiloto.Models.Util;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewActivityDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements ActivityListener, NavigationView.OnNavigationItemSelectedListener, InvestedTimeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.fab_add_activity_item)
    FloatingActionButton fab;

    @BindView(R.id.tv_total_time_invested)
    TextView ti_total;

    private List<InvestedTimeItem> investedTimeItems;
    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;
    private ProgressDialog progressDialog;

    private List<ActivityItem> activityItemsWeek;
    private BarChart chart;
    private ArrayList<BarEntry> BARENTRY;
    private ArrayList<String> BarEntryLabels;
    private LinearLayout graphLayout;

    private int tempoTotal = 0;
    private RecyclerView recyclerViewActivities;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient client;
    private GoogleApiClient mGoogleApiClient;

    private BarChart histChart;
    private List<BarEntry> histEntries;
    private LinearLayout histChartLayout;

    private LineChart lineChart;

    private PieChart pieChart;
    private ArrayList<Float> yDataPieChart = new ArrayList<Float>();
    private String[] xDataPieChart = {"BAIXA", "MÉDIA", "ALTA"};


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
        investedTimeItems = new ArrayList<>();

        setRecycleViews();

        // pega todos os Ti do firebase para o grafico do historico.
        FirebaseController.getInstance().retrieveAllInvestedTimeItems(mDatabase, investedTimeItems, HomeActivity.this);

        activityItemsWeek = new ArrayList<>();

        //Declaração das paradas pra gerar o gráfico
        chart = (BarChart) findViewById(R.id.chart1);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        //Declaração das paradas pra gerar o gráfico do Historico
        histChart = (BarChart) findViewById(R.id.histchart);
        histEntries = new ArrayList<BarEntry>();

        // LineChart
        lineChart = (LineChart) findViewById(R.id.lineChart);

        //Aqui acontece a mágica da plotagem do gráfico
        sortListWeek();
        graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        graphLayout.setVisibility(View.GONE);

        histChartLayout = (LinearLayout) findViewById(R.id.hist_graph_layout);
        histChartLayout.setVisibility(View.GONE);


        //Configuração do PieChart
        pieChart = (PieChart) findViewById(R.id.chart2);


        pieChart.setUsePercentValues(true);
        pieChart.setDescription("TEMPO INVESTIDO POR PRIORIDADE");

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e == null) {
                    return;
                } else {
                    Toast.makeText(HomeActivity.this, xDataPieChart[e.getXIndex()] + " = " +
                            e.getVal() + "%", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected() {
            }
        });

        addData();

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

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

        super.onBackPressed();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        float sumTimeTotal = 0;
        float sumTimeInvestPriorityMin = 0;
        float sumTimeInvestPriorityMed = 0;
        float sumTimeInvestPriorityMax = 0;

        for (int i = 0; i < activityItemsWeek.size(); i++) {
            if (activityItemsWeek.get(i).getPrioridade() == Util.PRIORIDADE_BAIXA) {
                sumTimeInvestPriorityMin += activityItemsWeek.get(i).getTotalInvestedTime();
            } else if (activityItemsWeek.get(i).getPrioridade() == Util.PRIORIDADE_MEDIA) {
                sumTimeInvestPriorityMed += activityItemsWeek.get(i).getTotalInvestedTime();
            } else {
                sumTimeInvestPriorityMax += activityItemsWeek.get(i).getTotalInvestedTime();
            }
        }

        sumTimeTotal = sumTimeInvestPriorityMax + sumTimeInvestPriorityMed + sumTimeInvestPriorityMin;

        float percBaixaPrioridade = (sumTimeInvestPriorityMin * 100) / sumTimeTotal;
        float percMediaPrioridade = (sumTimeInvestPriorityMed * 100) / sumTimeTotal;
        float percAltaPrioridade = (sumTimeInvestPriorityMax * 100) / sumTimeTotal;

        yDataPieChart.add(percBaixaPrioridade);
        yDataPieChart.add(percMediaPrioridade);
        yDataPieChart.add(percAltaPrioridade);

        ArrayList<Float> newyDataPieChart = new ArrayList<Float>();

        int n = yDataPieChart.size();
        int k = 3; // número de opções (Prioridade baixa, média e alta)

        for (int i = n - k; i < n; i++) {
            newyDataPieChart.add(yDataPieChart.get(i));
        }

        for (int i = 0; i < newyDataPieChart.size(); i++) {
            if (newyDataPieChart.get(i) >= 0.0) {
                yVals1.add(new Entry(newyDataPieChart.get(i), i));
            }
        }

        for (int i = 0; i < xDataPieChart.length; i++) {
            xVals.add(xDataPieChart[i]);
        }

        //criando pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "PRIORIDADES");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        //adicionando cores
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c: Util.MY_COLORS_PIE_PRIORIDADE) colors.add(c);

        dataSet.setColors(colors);

        //instanciando pieData object agora
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.rgb(106,106,106));

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_show_activities) {
            graphLayout.setVisibility(View.INVISIBLE);
            histChartLayout.setVisibility(View.INVISIBLE);
            recyclerViewActivities.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_show_graph) {

            graphLayout.setVisibility(View.VISIBLE);
            histChartLayout.setVisibility(View.INVISIBLE);
            recyclerViewActivities.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();
            ti_total.setText("Tempo total investido: " + tempoTotal + " horas");

        } else if (id == R.id.nav_general_report) {
            graphLayout.setVisibility(View.INVISIBLE);
            histChartLayout.setVisibility(View.VISIBLE);
            recyclerViewActivities.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();

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

        ImageView imageUser = (ImageView) header.findViewById(R.id.profile_image);
        TextView nameUser = (TextView) header.findViewById(R.id.nome);
        TextView emailUser = (TextView) header.findViewById(R.id.email);

        Picasso.with(header.getContext())
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .into(imageUser);
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
        receiverActivity(statusCode, resp);
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
            addData();
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {
        receiverActivity(statusCode, resp);
    }

    /**
     * Esse código está protegido pelas Leis de Deus, pq só ele sabe como isso ta funcionando apenas desse jeito.
     * Ass: Lúcio
     */
    private void itensOfWeekAndGraph() {

        int cont = 0;

        tempoTotal = 0;

        String[] values = new String[activityItems.size()];

        for (ActivityItem activityItem : activityItems) {

            if (activityItem.getTotalInvestedTimeWeek() > 0) {
                values[cont] = activityItem.getTitle();
                BARENTRY.add(new BarEntry(cont, activityItem.getTotalInvestedTimeWeek()));
                tempoTotal += activityItem.getTotalInvestedTimeWeek();
                cont++;
            }
        }

        // formatação do eixo x
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        ArrayList<BarEntry> listaBar = new ArrayList<>();
        // formatação do eixo y (esquerda)
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setValueFormatter(new MyYAxisValueFormatter());
        yAxis.setGranularity(1f);

        // ocultar eixo y (direita)
        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        BarDataSet bardataset = new BarDataSet(BARENTRY, "Total de horas por semana");
        BarData BARDATA = new BarData(bardataset);

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        BARDATA.setBarWidth(0.9f);
        chart.setData(BARDATA);
        chart.setFitBars(true);
        chart.animateY(3000);
        chart.invalidate();
    }

    private void plotHistChart() {
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

        histEntries.add(new BarEntry(0f, totalCurrentWeek));
        histEntries.add(new BarEntry(1f, totalLastWeek));
        histEntries.add(new BarEntry(2f, totalLastLastWeek));

        String[] values = new String[] { "Atual", "Passada", "Retrasada" };
        XAxis xAxis = histChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = histChart.getAxisLeft();
        yAxis.setValueFormatter(new MyYAxisValueFormatter());
        yAxis.setGranularity(1f);

        YAxis yRAxis = histChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        BarDataSet barDataSet = new BarDataSet(histEntries, "Total de horas por semana");
        BarData barData = new BarData(barDataSet);

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barData.setBarWidth(0.9f);
        histChart.setData(barData);
        histChart.setFitBars(true);
        histChart.animateY(3000);
        histChart.invalidate();

        // LineChart
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

    @Override
    public void receiverTi(int statusCode, String resp) {

    }

    @Override
    public void receiverTi(int statusCode, List<InvestedTimeItem> investedTimeItems, String resp) {
        progressDialog.dismiss();
        if (statusCode != 200) {
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            plotHistChart();
        }
    }
}
