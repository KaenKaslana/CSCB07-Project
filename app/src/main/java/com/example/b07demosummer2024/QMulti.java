package com.example.b07demosummer2024;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public class QMulti extends QAnswerFrag{

    protected String[] options;
   // RadioGroup questionGroup;
   protected LinearLayout questionGroup;

   protected HashSet<String> selected;
IListenClick listen;
    boolean multi;
    protected RadioButton currentSelected = null;
    public static QMulti CreateText( String[] options, boolean multi){
        QMulti spinToWin = new QMulti();
        spinToWin.multi= multi;
        spinToWin.options = options;
        return spinToWin;

    }
    public static QMulti CreateText( String[] options, boolean multi, IListenClick click){
        QMulti spinToWin = CreateText(options, multi);
        spinToWin.listen = click;
        return spinToWin;

    }
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_select_fragment, container, false);
        questionGroup = view.findViewById(R.id.AnswerGroup);
        selected = new HashSet<>();


        for(int i = 0; i < options.length; i ++) {
            RadioButton option = new RadioButton(getContext());
            option.setText(options[i]);
            option.setId(i);

            option.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    if(multi) {
                        if (option.isChecked()) {
                            selected.remove(option.getText().toString());
                            //Log.d("g",""+option.getId());
                            option.setChecked(false);
                        } else {
                            selected.add(option.getText().toString());
                            option.setChecked(true);

                        }
                    } else {
                        // fire event
                        if(listen!=null) {listen.Click(option.getText().toString());}
                            if(currentSelected != null) {
                                currentSelected.setChecked(false);
                                selected.remove(option.getText().toString());

                            }else {
                                currentSelected = option;
                            }
                        selected.add(option.getText().toString());
                        currentSelected = option;
                            currentSelected.setChecked(true);
                    }


                    }

                    return true;
                }
            });
        //    questionGroup.addView(option);
            questionGroup.addView(option);
        }
       /* questionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton answer = view.findViewById(i);
                    selected.add(answer.getText().toString());


            }
        });*/

        return view;
    }
    public void onDestroyView() {
        super.onDestroyView();
        selected = null;
       // questionGroup = null;

        options = null;
    }

    @Override
    public ArrayList<String> NotifyListener() {

        ArrayList<String> ret = new ArrayList<String>();
        ret.addAll(selected);
        return ret;
    }

}
