package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A fragment for answering in free form text
 *
 */
public class QFreeText extends QAnswerFrag{

    EditText editText;
    // initialization
    /**
     * Instance method
     * @return instance
     */
    public static QFreeText CreateText( ){
        QFreeText spinToWin = new QFreeText();
        return spinToWin;

    }
    // initialization

    //No crazy implemenation, just a text box
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.free_form_text, container, false);
        editText = view.findViewById(R.id.AnswerText);

        return view;
    }
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * returns a list with one free form answer, returns an empty list for an empty answer
     * @return the list
     */
    @Override
    public ArrayList<String> NotifyListener() {
        ArrayList<String> list = new ArrayList<String>();
        String str = editText.getText().toString();
        if(!str.equals("")) {
            list.add(editText.getText().toString());

        }
    return list;
    }
}
