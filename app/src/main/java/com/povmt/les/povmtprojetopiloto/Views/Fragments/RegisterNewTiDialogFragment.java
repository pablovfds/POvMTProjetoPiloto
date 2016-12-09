package com.povmt.les.povmtprojetopiloto.Views.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.InvestedTimeListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewTiDialogFragment extends DialogFragment implements InvestedTimeListener {

    @BindView(R.id.input_time_ti) TextInputEditText inputTimeTi;
    @BindView(R.id.input_date_ti) EditText inputDateTi;

    private ActivityItem activityItem;
    private DatabaseReference mDatabase;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private OnCompleteListener mListener;



    public static RegisterNewTiDialogFragment newInstance(ActivityItem activityItem) {

        Bundle args = new Bundle();
        args.putSerializable("activityId", activityItem);
        RegisterNewTiDialogFragment fragment = new RegisterNewTiDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        activityItem = (ActivityItem) bundle.getSerializable("activityId");

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public RegisterNewTiDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Registrar novo Ti");

        inputTimeTi.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_new_ti_dialog, container, false);
        ButterKnife.bind(this, view);

        inputDateTi.setInputType(InputType.TYPE_NULL);

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRENCH);
                inputDateTi.setText(sdf.format(myCalendar.getTime()));
            }
        };

        return view;
    }

    @OnClick(R.id.buttonCancel)
    public void dismissDialog(){
        this.dismiss();
    }

    @OnClick(R.id.buttonCreate)
    public void createNewTiDialog(){



        inputTimeTi.setError(null);
        inputDateTi.setError(null);
        int investedTime = Integer.valueOf(inputTimeTi.getText().toString());
        String createdAt = inputDateTi.getText().toString();

        if(investedTime <= 0.0){
            inputTimeTi.setError("Insira um tempo investido válido.");
        } else if (createdAt.isEmpty()){
            inputDateTi.setError("Insira uma data para o tempo investido");
        } else {
            InvestedTimeItem tiItem = new InvestedTimeItem(investedTime, createdAt);

            Calendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setCalendar(cal);
            activityItem.setUpdatedAt(createdAt);
//            activityItem.setUpdatedAt(dateFormat.format(cal.getTime()));
            activityItem.addNewInvestedTime(tiItem);

            FirebaseController.getInstance()
                    .insertTi(activityItem, tiItem, mDatabase, this);




        }
    }

    @OnClick(R.id.input_date_ti)
    public void openDatePicker(){
        DatePickerDialog dp = new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    @Override
    public void receiverTi(int statusCode, String resp) {
        if (statusCode != 200){
            Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), resp, Toast.LENGTH_SHORT).show();
            this.mListener.onComplete(activityItem);
        }

        this.dismissDialog();
    }

    @Override
    public void receiverTi(int statusCode, List<InvestedTimeItem> investedTimeItems, String resp) {
        receiverTi(statusCode, resp);
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(ActivityItem item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
