package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QSpinnerFragment extends QAnswerFrag {
   protected String [] myOptions;
    Spinner spinner;
   public static QSpinnerFragment CreateSpinner(String[] myOptions){
       QSpinnerFragment spinToWin = new QSpinnerFragment();
       spinToWin.myOptions = myOptions;
       return spinToWin;

   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spinner, container, false);

         spinner = view.findViewById(R.id.spinner);
          ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                 android.R.layout.simple_spinner_item, myOptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                NotifyListener();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }
    public void onDestroyView() {
        super.onDestroyView();
        spinner = null;
    }

    @Override
    public ArrayList<String> NotifyListener() {
        ArrayList<String> list = new ArrayList<String>();
       list.add(spinner.getSelectedItem().toString());
       return list;
    }
}