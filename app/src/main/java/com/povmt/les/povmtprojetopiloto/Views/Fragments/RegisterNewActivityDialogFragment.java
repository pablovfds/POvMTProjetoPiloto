package com.povmt.les.povmtprojetopiloto.Views.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewActivityDialogFragment extends DialogFragment implements ActivityListener {

    @BindView(R.id.input_name_activity_item) TextInputEditText inputTitle;
    @BindView(R.id.input_description_activity_item) TextInputEditText inputDescription;

    private DatabaseReference mDatabase;

    public RegisterNewActivityDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("activities");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Adicionar nova atividade");

        inputTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register_new_activity_dialog, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_cancel_create_activity)
    public void dismissDialog(){
        this.dismiss();
    }

    @OnClick(R.id.btn_create_activity)
    public void createActivity(){
        inputTitle.setError(null);
        inputDescription.setError(null);
        String titleActivity = inputTitle.getText().toString();
        String descriptionActivity = inputDescription.getText().toString();

        if(titleActivity.isEmpty()){
            inputTitle.setError("Insira um título para a atividade");
        } else if (descriptionActivity.isEmpty()){
            inputDescription.setError("Insira uma descrição para a atividade");
        } else {
            ActivityItem activityItem = new ActivityItem(titleActivity, descriptionActivity);

            FirebaseController.getInstance().insertActivity(activityItem, mDatabase, this);
        }
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, String resp) {
        if (statusCode != 200){
            Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
        }
        this.dismissDialog();
    }

}
