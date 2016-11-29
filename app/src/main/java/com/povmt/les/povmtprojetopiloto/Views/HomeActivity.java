package com.povmt.les.povmtprojetopiloto.Views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Adapters.ActivityItemAdapter;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class HomeActivity extends AppCompatActivity implements ActivityListener, NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.recycleview_activities) RecyclerView recyclerViewActivities;
    @BindView(R.id.tv_total_time_invested) TextView ti_total;
    private TextView nameUser;
    private TextView emailUser;

    private RecyclerView recyclerView;
    private List<ActivityItem> activityItems;
    private ActivityItemAdapter adapter;
//    private ProgressDialog progressDialog;
    private List<ActivityItem> activityItemsWeek;

    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    private LinearLayout graphLayout;
    private float tempoTotal = 0;
    FloatingActionButton fab;

    public HomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        ButterKnife.bind(this);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_activity_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize Firebase Auth and Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        activityItems = new ArrayList<>();
        retrieveAllActivities();

        activityItemsWeek = new ArrayList<>();

        /**
         * Declaraçao de variaveis para geraçao de grafico
         */
        chart = (BarChart) findViewById(R.id.chart1);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();

        /**
         * Plotangem do grafico
         */
        sortListWeek();
        graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        graphLayout.setVisibility(View.INVISIBLE);

        /**
         * Parte do logout
         */
        mAuth = FirebaseAuth.getInstance();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
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
        final TextInputEditText inputInvestedTime = (TextInputEditText) dialog.findViewById(R.id.input_invested_time);
        final EditText inputDateInvestedTime = (EditText) dialog.findViewById(R.id.input_date_invested_time);

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
        recyclerView = (RecyclerView) findViewById(R.id.recycleview_activities);
        adapter = new ActivityItemAdapter(this, activityItems);
        recyclerView.setHasFixedSize(true);
//        progressDialog.show();

        FirebaseController.getInstance().retrieveAllActivities(mDatabase, activityItems, HomeActivity.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems) {
//        progressDialog.dismiss();
        if (statusCode != 200){
            Toast.makeText(this, "Erro em carregar lista", Toast.LENGTH_SHORT).show();
        } else {
            adapter.update(activityItems);
            itensOfWeekAndGraph();
        }
    }

    @Override
    public void receiverActivity(int statusCode, String resp) {
//        progressDialog.dismiss();
        if (statusCode != 200){
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        }
    }

    private void itensOfWeekAndGraph(){
        int cont = 0;

        for (ActivityItem activityItem : activityItems) {
            if(activityItem.isActivityWeek()){
                if(!activityItemsWeek.contains(activityItem)){
                    activityItemsWeek.add(activityItem);

                    BarEntryLabels.add(activityItem.getTitle());
                    BARENTRY.add(new BarEntry(activityItem.getTotalInvestedTime(), cont));
                    tempoTotal += activityItem.getTotalInvestedTime();
                }
            }

            cont ++;
        }

        Bardataset = new BarDataSet(BARENTRY, "Atividades");
        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(BARDATA);
        chart.animateY(3000);
    }

    private void sortListWeek(){
        Collections.sort(activityItemsWeek);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_show_activities) {
            graphLayout.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_show_graph) {
            graphLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            ti_total.setText("Tempo investido : " + tempoTotal + " horas");

        }  else if (id == R.id.nav_general_report) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            signOut();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Logout do aplicativo
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    /**
     * Carregar informaçoes do usuario logado
     */
    private void infoUser() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        nameUser = (TextView) header.findViewById(R.id.nome);
        emailUser = (TextView) header.findViewById(R.id.email);

        nameUser.setText(mAuth.getCurrentUser().getDisplayName());
        emailUser.setText(mAuth.getCurrentUser().getEmail());
    }
}