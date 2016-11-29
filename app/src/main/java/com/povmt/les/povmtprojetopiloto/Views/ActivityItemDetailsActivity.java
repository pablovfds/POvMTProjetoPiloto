package com.povmt.les.povmtprojetopiloto.Views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTime;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityItemDetailsActivity extends AppCompatActivity implements InvestedTimeListener {

    @BindView(R.id.textViewTitle) TextView textViewTitle;

    @BindView(R.id.textViewDescription) TextView textViewDescription;

    @BindView(R.id.textViewCreatedAt) TextView textViewCreatedAt;

    @BindView(R.id.textViewUpdatedAt) TextView textViewUpdatedAt;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;

    private ActivityItem activityItem;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando dados");
        progressDialog.show();

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            activityItem = (ActivityItem)extras.get("activityItem");
            if (activityItem != null) {
                textViewTitle.setText(activityItem.getTitle());
                textViewDescription.setText(activityItem.getDescription());
                textViewCreatedAt.setText(activityItem.getCreatedAt());
                textViewUpdatedAt.setText(activityItem.getUpdatedAt());
                progressDialog.dismiss();
            }
        }

        //Fazer tela de detalhes da atividade
        //Fazer floating button para adição de novos tempos investidos
    }

    @OnClick(R.id.fab_add_invested_time)
    public void addNewInvestedTime(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_register_ti);
        dialog.setTitle("Adicionar nova atividade");

        Button buttonCreate = (Button) dialog.findViewById(R.id.buttonCreate);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
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
                int time = Integer.valueOf(inputInvestedTime.getText().toString());
                String createdAt = inputDateInvestedTime.getText().toString();

                InvestedTime investedTime = new InvestedTime(time);
                investedTime.setCreatedAt(createdAt);
                activityItem.addNewInvestedTime(investedTime);
                FirebaseController.getInstance()
                        .insertTi(activityItem, investedTime, mDatabase, ActivityItemDetailsActivity.this);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void receiverTi(int statusCode, String resp) {
        if (statusCode != 200){
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        }
    }
}
