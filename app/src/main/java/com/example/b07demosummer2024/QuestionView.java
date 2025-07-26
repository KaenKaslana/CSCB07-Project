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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

// multi select, multi select with free form text, free form text, free form date
public class QuestionView extends AppCompatActivity {
    String path = "WarmUp";
    public static final String WarmUpPath = "WarmUp";
    public static final String FollowUpPath = "Follow";
    public static final String StillPath = "Still";
    public static final String PostPath = "Post";

    public static final String PlanningPath = "Planning";


    String[] subText = {"enter a code word", "enter a temporary shelter", "enter a legal order", "enter equipment"};
    static String[][] warmupOptions = {
            {"Still in a relationship", "Planning to leave", "Post-separation"},

            {"Toronto", "Vancouver", "Ottawa", "Edmonton"},
            {},
            {"Family", "Roommates", "Alone", "Partner"},
            {"Yes", "No"}
    };
    static String[][] stillOptions = {
            {"Physical", "Emotional", "Financial", "Other"},
            {"Yes", "No"}, {}
    };
    static String[][] planOptions = {
            {}, {"Yes", "No"},
            {}, {"Yes", "No"}
    };
    static String[][] postOptions = {
            {"Yes", "No"},
            {"Yes", "No"},
            {"Yes", "No"}
    };
    static String[][] followUpOptions = {
            {"Counselling", "Legal aid", "Financial guidance", "Co-parenting resources"}
    };
    public static Section warmupSection;
    public static Section stillSection;
    public static Section planSection;
    public static Section postSection;
    public static Section followSection;





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

    static String[] question = null;

    public static String GetQText(int i) {


        return question[i];
    }
    protected void LoadSection(int i, boolean loadFirst) {
        final int[] k = {0};


        if (i > 2 || i == -1) {

        } else {
            switch (i) {
                case 0:
                    options = warmupOptions;
                    path = "WarmUp";
                    question = warmupSection.questions;
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
                            for (DataSnapshot child : snapshot.child("" + 1).child("" + 1).getChildren()) {
                                String choice = child.getValue(String.class);
                                Log.d("guh", choice);
                                if (choice.equals("Post-separation")) {
                                    options = postOptions;
                                    path = "Post";
                                    question = postSection.questions;
                                } else if (choice.equals("Planning to leave")) {
                                    options = planOptions;
                                    path = "Planning";
                                    question = planSection.questions;

                                } else {
                                    options = stillOptions;
                                    path = "Still";
                                    question = stillSection.questions;
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
                    question = followSection.questions;
                    loadPagerAdapter(options.length);
                    if (loadFirst == false) {
                        k[0] = options.length - 1;
                    }
                    questionPager.setCurrentItem(k[0]);

                    LoadAnswer(k[0]);
                    break;


            }
            questionState = i;

            //loadPagerAdapter(options.length);
            //questionPager.setCurrentItem(0);
            // Log.d("gupper", question[1]);
            //LoadAnswer(0);

        }


    }

    protected void loadPagerAdapter(int length) {
        fsa = new QuestionFSA(this, this, length);
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
                if (answers == null) {
                    return;
                }
                StoreAnswer(i, answers);
                if (i < fsa.getItemCount() - 1) {


                    if (!answers.isEmpty()) {
                        //Log.d("answers", answers.toString());
                        questionPager.setCurrentItem(i + 1);
                        LoadAnswer(i + 1);
                    }
                } else {
                    LoadSection(questionState + 1, true);
                }
            }
        });
        prev.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                int i = questionPager.getCurrentItem();
                if (i > 0) {
                    questionPager.setCurrentItem(i - 1);
                    LoadAnswer(i - 1);
                } else {
                    LoadSection(questionState - 1, false);
                }
            }
        });
        LoadAnswer(0);
    }

    protected void StoreAnswer(int i, ArrayList<String> answers) {

        DatabaseReference dbRef = db.getReference("Q&A/" + path);
        dbRef.child("" + (i + 1)).child("" + 1).push().setValue(answers.get(0));
        for (int j = 1; j < answers.size(); j++) {
            dbRef.child("" + (i + 1)).child("" + 2).push().setValue(answers.get(j));
        }
    }

    protected void LoadAnswer(int i) {


        DatabaseReference dbRef = db.getReference("Q&A/" + path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.child("" + (i + 1)).child("" + 1).getChildren()) {
                    child.getRef().removeValue();
                }
                for (DataSnapshot child : snapshot.child("" + (i + 1)).child("" + 2).getChildren()) {
                    child.getRef().removeValue();
                }

                int answerType = snapshot.child("" + (i + 1)).child("" + 0).getValue(Integer.class);
                // Log.d("guh", "" + (answerType));
                // Log.d("guh", "" + (i+1));
                switch (answerType) {
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
                        if (path.equals("WarmUp")) {
                            text = subText[0];
                        }
                        if (path.equals("Planning")) {
                            text = subText[1];
                        }
                        if (path.equals("Post")) {
                            if (i + 1 == 2) {
                                text = subText[2];

                            } else {
                                text = subText[3];

                            }
                        }
                        currentAnswerFrag = QMultiToText.CreateText(options[i], text);
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

    public static void loadQuestionsFromAssets(Context context) {
        try {
            InputStream stream = context.getAssets().open("questions.json");
            InputStreamReader reader = new InputStreamReader(stream);
            int size = stream.available();
            byte[] buffer = new byte[size];

            stream.read(buffer);
            stream.close();
            String file = new String(buffer, StandardCharsets.UTF_8);
            // Parse root as JsonObject
            JSONObject root = new JSONObject(file);
            warmupSection = LoadSection("warmup_array", root);
            stillSection = LoadSection("still_in_array", root);
            planSection = LoadSection("plan_leave_array", root);
            postSection = LoadSection("post_array", root);
            followSection = LoadSection("follow_up_array", root);


            reader.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static Section LoadSection(String sect, JSONObject root) throws Exception {
        JSONArray warmupArray = root.getJSONArray(sect);
        Section res = new Section(warmupArray.length());
        for (int i = 0; i < warmupArray.length(); i++) {
            JSONObject item = warmupArray.getJSONObject(i);

            String question = item.getString("question");
            res.questions[i] = question;
            JSONObject tips = item.getJSONObject("tips");

            Iterator<String> keys = tips.keys();
            while (keys.hasNext()) {

                String key = keys.next();
                String tip = tips.getString(key);
                res.tips.get(i).put(key, tip);
                //Log.d("ParsedTip", "Question: " + question + " | Key: " + key + " | Tip: " + tip);
            }
        }
        return res;
    }

}
class Section {
    String[] questions;
    ArrayList<Hashtable<String, String>> tips;
    public Section(int arr) {
        questions = new String[arr];
        tips = new ArrayList<Hashtable<String, String>>();
        for(int i = 0; i < arr; i++) {
            tips.add(new Hashtable<String, String>());
        }
    }

}


