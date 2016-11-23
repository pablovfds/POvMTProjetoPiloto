package com.povmt.les.povmtprojetopiloto.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Adapters.InvestedTimeAdapter;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityItemDetailsActivity extends AppCompatActivity implements ActivityListener {

    @BindView(R.id.recycleview_activities_details) RecyclerView recyclerViewActivitiesDetails;

    @BindView(R.id.textViewTitle) TextView textViewTitle;

    @BindView(R.id.textViewDescription) TextView textViewDescription;

    @BindView(R.id.textViewCreatedAt) TextView textViewCreatedAt;

    @BindView(R.id.textViewUpdatedAt) TextView textViewUpdatedAt;

    private List<InvestedTime> investedTimes;
    private InvestedTimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ButterKnife.bind(this);

        investedTimes = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            ActivityItem activityItem = (ActivityItem)extras.get("activityItem");
            if (activityItem != null) {
                textViewTitle.setText(activityItem.getTitle());
                textViewDescription.setText(activityItem.getDescription());
                textViewCreatedAt.setText(activityItem.getCreatedAt());
                textViewUpdatedAt.setText(activityItem.getUpdatedAt());
                investedTimes = activityItem.getInvestedTimeList();
            }
        }

        adapter = new InvestedTimeAdapter(this, investedTimes);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerViewActivitiesDetails.setLayoutManager(llm);
        recyclerViewActivitiesDetails.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        //Fazer tela de detalhes da atividade
        //Fazer floating button para adição de novos tempos investidos
    }

    @OnClick(R.id.fab_add_invested_time)
    public void addNewInvestedTime(){

    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems) {

    }

    @Override
    public void receiverActivity(int statusCode, boolean resp) {

    }
}
