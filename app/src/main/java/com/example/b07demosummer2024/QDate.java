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

/**
 * A fragment for providing an answer in the format of date
 *
 */
public class QDate extends QAnswerFrag{

    DatePickerDialog dates;
    Button button;
    /**
     * Instantiation method
     * @return instance
     */
    public static QDate CreateText( ){
        QDate spinToWin = new QDate();

        return spinToWin;

    }
    // initialization

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_fragment, container, false);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            //when as date is selected, set the button's text to the format specified in the questions list
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 +=1;
                String date = i +" " +i1+" " +i2;
                button.setText(date);
            }
        };
        // create date picker dialog, with initial date being today :)
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dates = new DatePickerDialog(getActivity(), R.style.Theme_B07DemoSummer2024,listener, year, month, day);


        // set text to the above initial date
        button = view.findViewById(R.id.DatePick);
        button.setText((year +" " + (month + 1) + " " +day).toString());
        button.setOnClickListener(new View.OnClickListener() {

            //when clicked, show the calendar for the user
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


    /**
     * return a list of one entry being the date
     * @return the list
     */
    @Override
    public ArrayList<String> NotifyListener() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(button.getText().toString());
        return list;
    }
}

