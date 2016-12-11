package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewTiDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityItemDetailsActivity extends AppCompatActivity implements
        RegisterNewTiDialogFragment.OnCompleteListener, ActivityListener{

    @BindView(R.id.textViewTitle) TextView textViewTitle;

    @BindView(R.id.textViewDescription) TextView textViewDescription;

    @BindView(R.id.textViewCreatedAt) TextView textViewCreatedAt;

    @BindView(R.id.textViewUpdatedAt) TextView textViewUpdatedAt;

    @BindView(R.id.textViewTotalTi) TextView textViewTotalTi;

    @BindView(R.id.imageViewActivityPhoto) ImageView mActivityPhoto;

    @BindView(R.id.toolbar) Toolbar toolbar;

    private ActivityItem activityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ButterKnife.bind(this);

        ProgressDialog progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            activityItem = (ActivityItem) extras.getSerializable("activityItem");

            if (activityItem != null){
                getSupportActionBar().setTitle(activityItem.getTitle());
                setTextViews(activityItem);
            }
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
        FirebaseController.getInstance().retrieveActivityImage(activityItem.getImageUrl(), this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {

    }

    @Override
    public void receiverImageUri(Uri uri) {
        Picasso.with(this).load(uri).resize(1800, 1800).centerCrop().into(mActivityPhoto);
    }

    @Override
    public void receiverActivity(int code, String s) {

    }
}
