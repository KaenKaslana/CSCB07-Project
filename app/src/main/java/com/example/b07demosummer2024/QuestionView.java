package com.example.b07demosummer2024;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

// multi select, multi select with free form text, free form text, free form date
public class QuestionView extends AppCompatActivity

{
    String path = "WarmUp";
    String[] subText = {"enter a code word", "enter a temporary shelter", "enter a legal order", "enter equipment"};
    static String[][] warmupOptions = {
        { "Still in a relationship", "Planning to leave", "Post-separation" },

        { "Toronto",               "Vancouver",         "Ottawa",         "Edmonton" },
        {},
        { "Family",                "Roommates",         "Alone",          "Partner" },
        { "Yes",                   "No" }
};
    static String[][] stillOptions = {
            { "Physical", "Emotional", "Financial", "Other" },
            { "Yes", "No" },{}
    };
    static String[][] planOptions = {
            {  }, {"Yes", "No"},
            {  },{"Yes", "No"}
    };
    static String[][] postOptions = {
            { "Yes", "No" },
            { "Yes", "No" },
            { "Yes", "No" }
    };
    static String[][] followUpOptions = {
            { "Counselling", "Legal aid", "Financial guidance", "Co-parenting resources" }
    };
    static int questionState = 0;
    protected ViewPager2 questionPager;
    protected Button next;
    protected Button prev;
    FirebaseDatabase db;
    int first = 0;
    protected QAnswerFrag currentAnswerFrag = null;
    // question types: 0 is multi select 1, 1 is multi select many, 2 is freetext
    //3 is spinner, 4 is date, 5 is multi to free
    FragmentStateAdapter fsa;
    String[][] options = warmupOptions;

    static String[]question = null;
    public static String GetQText(int i) {


        return question[i];
    }
    protected  void LoadSection(int i, boolean loadFirst) {
        final int[] k = {0};



        if(i > 2 || i == -1) {

            }  else {
                switch(i) {
                    case 0:
                        options = warmupOptions;
                        path = "WarmUp";
                        question = getResources().getStringArray(R.array.warmup_array);
                        loadPagerAdapter(options.length);
                        if (loadFirst == false) {
                            k[0] = options.length - 1;
                        }
                        questionPager.setCurrentItem(k[0]);
                        LoadAnswer(k[0]);
                        break;
                    case 1:
                        DatabaseReference dbRef = db.getReference("Q&A/WarmUp");
                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.child("" + 1).child(""+1).getChildren()) {
                                    String choice = child.getValue(String.class);
                                    Log.d("guh",choice);
                                    if(choice.equals("Post-separation")) {
                                    options = postOptions;
                                    path = "Post";
                                    question = getResources().getStringArray(R.array.post_array);
                                    }else if (choice.equals("Planning to leave")) {
                                        options = planOptions;
                                        path = "Planning";
                                        question = getResources().getStringArray(R.array.plan_leave_array);

                                    }
                                        else {
                                        options = stillOptions;
                                        path = "Still";
                                        question = getResources().getStringArray(R.array.still_in_array);
                                    }

                                    break; // only need the first one
                                }
                                if (loadFirst == false) {
                                    k[0] = options.length - 1;
                                }
                                loadPagerAdapter(options.length);
                                questionPager.setCurrentItem(k[0]);

                                LoadAnswer(k[0]);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case 2:
                    options = followUpOptions;
                    path = "Follow";
                    question = getResources().getStringArray(R.array.follow_up_array);
                        loadPagerAdapter(options.length);
                        if (loadFirst == false) {
                            k[0] = options.length - 1;
                        }
                        questionPager.setCurrentItem(k[0]);

                        LoadAnswer(k[0]);
                        break;


                }
                questionState =i;

               //loadPagerAdapter(options.length);
                //questionPager.setCurrentItem(0);
               // Log.d("gupper", question[1]);
               //LoadAnswer(0);

            }


    }
    protected void loadPagerAdapter(int length) {
        fsa = new QuestionFSA(this,this,length);
        questionPager.setAdapter(null);

        questionPager.setAdapter(fsa);
    }
    //QuestionPresenter presenter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Log.d("gupper", "created");
        db = FirebaseDatabase.getInstance("https://cscb07-group-2-default-rtdb.firebaseio.com/");

        setContentView(R.layout.question_activity);
        questionPager = (ViewPager2) findViewById(R.id.QVP2);
        next = findViewById(R.id.QuestionNext);
        prev = findViewById(R.id.QuestionPrevious);

        questionPager.setUserInputEnabled(false); //disable swiping
        loadPagerAdapter(warmupOptions.length);
        question = getResources().getStringArray(R.array.warmup_array);



        //presenter = new QuestionPresenter(this, new QuestionModel(presenter));

        next.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                int i = questionPager.getCurrentItem();
                ArrayList<String> answers = GetAnswer();
                if(answers ==null) {return;}
                StoreAnswer(i, answers);
                if (i < fsa.getItemCount() - 1) {





                    if(!answers.isEmpty()) {
                        //Log.d("answers", answers.toString());
                        questionPager.setCurrentItem(i + 1);
                        LoadAnswer(i+1);
                    }
                } else {
                    LoadSection(questionState+1, true);
                }
            }
        });
        prev.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                int i = questionPager.getCurrentItem();
                if (i > 0) {
                    questionPager.setCurrentItem(i - 1);
                   LoadAnswer(i-1);
                } else{
                    LoadSection(questionState-1,false);
                }
            }
        });
        LoadAnswer(0);
    }
    protected void StoreAnswer(int i , ArrayList<String> answers) {

        DatabaseReference dbRef = db.getReference("Q&A/"+ path);
        for(int j=0; j < answers.size(); j++) {
            dbRef.child("" + (i + 1)).child("" + 1).push().setValue(answers.get(j));
        }
    }
    protected void LoadAnswer(int i) {


        DatabaseReference dbRef = db.getReference("Q&A/" + path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot child : snapshot.child(""+(i+1)).child(""+1).getChildren()) {
                    child.getRef().removeValue();
                }

                int answerType = snapshot.child("" + (i+1)).child("" + 0).getValue(Integer.class);
               // Log.d("guh", "" + (answerType));
               // Log.d("guh", "" + (i+1));
                switch(answerType) {
                    case 0:
                    currentAnswerFrag = QMulti.CreateText(options[i], false);
                        break;

                    case 1:
                        currentAnswerFrag = QMulti.CreateText(options[i], true);
                        break;
                    case 2:

                    currentAnswerFrag = QFreeText.CreateText();
                    break;
                    case 3:
                        currentAnswerFrag = QSpinnerFragment.CreateSpinner(options[i]);

                        break;

                    case 4:
                        currentAnswerFrag = QDate.CreateText();
                        break;

                    case 5:
                        String text = null;
                        if(path.equals("WarmUp")) {
                            text = subText[0];
                        }
                        if(path.equals("Planning")) {
                            text= subText[1];
                        }
                        if(path.equals("Post")) {
                            if(i+1 == 2) {
                                text= subText[2];

                            }
                            else{
                                text= subText[3];

                            }
                        }
                        currentAnswerFrag = QMultiToText.CreateText(options[i],text);
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.AnswerContainer, currentAnswerFrag).commit();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //  currentAnswerFrag = QMulti.CreateText(new String[]{"hi", "silly"},false);
        //  getSupportFragmentManager().beginTransaction().replace(R.id.AnswerContainer, currentAnswerFrag).commit();

    }

    public ArrayList<String> GetAnswer() {
        ArrayList<String> answer = currentAnswerFrag.NotifyListener();

        return answer;
    }


}



