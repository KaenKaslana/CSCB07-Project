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
/**
 *
 Bryce Chen
 *
 * Question type for multiple choice for selecting one answer and generating a text box
 *
 *
 */

public class QMultiToText extends QAnswerFrag implements  IListenClick{

    QMulti multi;
    QFreeText input;
    String[] options;
    LinearLayout layout;
    TextView sub;
    boolean yes = false;
    String text;
    //initialization
    /**
     * Instance method
     * @param options The options you can pick
     * @param text Additional free form text prompt
     * @return instance
     */
    public static QMultiToText CreateText( String[] options, String text){
        QMultiToText spinToWin = new QMultiToText();
        spinToWin.text = text;
        spinToWin.options=options;
        return spinToWin;

    }
    //initialization

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_text_fragment, container, false);

        layout = view.findViewById(R.id.MultiTextGroup);
        sub = view.findViewById(R.id.SubText);

        return view;
    }
    //initialization
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //create a multiple choice fragment
        multi = QMulti.CreateText(options,false, this);
        getChildFragmentManager().beginTransaction().replace(R.id.MultiText1, multi).commit();

    }
    /**
     * returns a list of all selected answers and a freeform text answer if there is one
     * @return the list
     */
        @Override
    public ArrayList<String> NotifyListener() {
            ArrayList<String> ret = new ArrayList<String>();
            ret.addAll(multi.NotifyListener());
            //add multiple choice answers and add text answers if applicable
            if(input != null) {
                if(input.NotifyListener().isEmpty()) {return null;}
                ret.addAll(input.NotifyListener());
            }

            return ret;
    }
    /**
     * create a free form text instance
     */
    void CreateText() {
        //create the text box fragment

        input = QFreeText.CreateText();
        getChildFragmentManager().beginTransaction().replace(R.id.MultiText2, input).commit();
        sub.setText(text);
    }
    /**
     * remove free form text
     */
    void DestroyText() {
        //destroy the text box fragment
        sub.setText("");

        if(input!=null) {
            getChildFragmentManager().beginTransaction().remove(input).commit();
            input = null;
        }
    }

    /**
     * Create a freeform text answer if the answer is "yes"
     * not the best implementation to be honest
     * @param  str given answer
     */
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
