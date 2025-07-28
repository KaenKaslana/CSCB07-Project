package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public class QMultiToText extends QAnswerFrag implements  IListenClick{

    QMulti multi;
    QFreeText input;
    String[] options;
    LinearLayout layout;
    TextView sub;
    boolean yes = false;
    String text;
    public static QMultiToText CreateText( String[] options, String text){
        QMultiToText spinToWin = new QMultiToText();
        spinToWin.text = text;
        spinToWin.options=options;
        return spinToWin;

    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_text_fragment, container, false);

        layout = view.findViewById(R.id.MultiTextGroup);
        sub = view.findViewById(R.id.SubText);

        return view;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {

        multi = QMulti.CreateText(options,false, this);
        getChildFragmentManager().beginTransaction().replace(R.id.MultiText1, multi).commit();

    }

        @Override
    public ArrayList<String> NotifyListener() {
            ArrayList<String> ret = new ArrayList<String>();
            ret.addAll(multi.NotifyListener());
            if(input != null) {
                if(input.NotifyListener().isEmpty()) {return null;}
                ret.addAll(input.NotifyListener());
            }

            return ret;
    }
    void CreateText() {
        input = QFreeText.CreateText();
        getChildFragmentManager().beginTransaction().replace(R.id.MultiText2, input).commit();
        sub.setText(text);
    }
    void DestroyText() {
        sub.setText("");

        if(input!=null) {
            getChildFragmentManager().beginTransaction().remove(input).commit();
            input = null;
        }
    }
    @Override
    public void Click(String str) {
        Log.d("chup","clicked");
        if(str.equals("Yes")) {

       CreateText();
        yes = true;
        }
        else {
       DestroyText();
        yes=false;
        }

    }
}
