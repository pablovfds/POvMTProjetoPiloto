package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewTiDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityItemDetailsActivity extends AppCompatActivity implements RegisterNewTiDialogFragment.OnCompleteListener{

    @BindView(R.id.textViewTitle) TextView textViewTitle;

    @BindView(R.id.textViewDescription) TextView textViewDescription;

    @BindView(R.id.textViewCreatedAt) TextView textViewCreatedAt;

    @BindView(R.id.textViewUpdatedAt) TextView textViewUpdatedAt;

    @BindView(R.id.textViewTotalTi) TextView textViewTotalTi;

    private ActivityItem activityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ButterKnife.bind(this);

        ProgressDialog progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            activityItem = (ActivityItem) extras.get("activityItem");
            getSupportActionBar().setTitle(activityItem.getTitle());
            setTextViews(activityItem);
            progressDialog.dismiss();
        }
    }

    @OnClick(R.id.fab_add_invested_time)
    public void addNewInvestedTime(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RegisterNewTiDialogFragment registerTiDialog = RegisterNewTiDialogFragment.newInstance(activityItem);
        registerTiDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        registerTiDialog.show(ft, "registerTiDialog");
    }


    @Override
    public void onComplete(ActivityItem item) {
        this.activityItem = item;
        setTextViews(activityItem);
    }

    private void setTextViews(ActivityItem activityItem){
        textViewTitle.setText(activityItem.getTitle());
        textViewDescription.setText(activityItem.getDescription());
        textViewCreatedAt.setText(activityItem.getCreatedAt());
        textViewUpdatedAt.setText(activityItem.getUpdatedAt());
        String totalTi = activityItem.getTotalInvestedTime() + " Hora(s)";
        textViewTotalTi.setText(totalTi);
    }
}
