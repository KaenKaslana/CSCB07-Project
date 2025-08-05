package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
/**
 *
 *Bryce Chen
 *
 *Class for displaying questions
 *
 *
 */

public class QuestionFrag extends Fragment {
    private static final String QUESTION_NUM_KEY = "HEHEHEHAWGRRRR";



    protected TextView textView;
  //  protected String questionText;
    int answerType = 0;
    public static QuestionFrag CreateQFrag(int position ) {
       // factory method needed since it lets android restore fragments, also why I use bundle :(
        QuestionFrag frag = new QuestionFrag();
        Bundle bundle = new Bundle();
        bundle.putInt(QUESTION_NUM_KEY, position);

        frag.setArguments(bundle);
        frag.answerType = 0;

        return frag;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textView = null;
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {
        //inflates the xml into an actual view and returns it


        View parent = inflater.inflate(R.layout.question_fragment, container, false);

        return parent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
        if(answerType == 0) {
        //on creation, just set the text based on its order (QUESTION_NUM_KEY)


            textView = view.findViewById(R.id.questionTextView);
           textView.setText(QuestionView.GetQText(getArguments().getInt(QUESTION_NUM_KEY)));
        }
    }



}



