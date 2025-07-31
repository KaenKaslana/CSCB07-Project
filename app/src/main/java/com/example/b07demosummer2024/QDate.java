package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QDate extends QAnswerFrag{

    DatePickerDialog dates;
    Button button;
    public static QDate CreateText( ){
        QDate spinToWin = new QDate();

        return spinToWin;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_fragment, container, false);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 +=1;
                String date = i +" " +i1+" " +i2;
                button.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dates = new DatePickerDialog(getActivity(), R.style.Theme_B07DemoSummer2024,listener, year, month, day);



        button = view.findViewById(R.id.DatePick);
        button.setText((year +" " + (month + 1) + " " +day).toString());
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                dates.show();
            }
        });
        return view;
    }
    public void onDestroyView() {
        super.onDestroyView();
        button = null;
        dates= null;
    }

    @Override
    public ArrayList<String> NotifyListener() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(button.getText().toString());
        return list;
    }
}

