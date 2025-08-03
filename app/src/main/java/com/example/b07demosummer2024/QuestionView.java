package com.example.b07demosummer2024;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *
 *Bryce Chen
 *
 *Class that manages the entire question quiz
 *
 *
 */
public class QuestionView extends Fragment {
    String path = "WarmUp";
    public static final String WarmUpPath = "WarmUp";
    public static final String FollowUpPath = "Follow";
    public static final String StillPath = "Still";
    public static final String PostPath = "Post";

    public static final String PlanningPath = "Planning";
    IQuizDone listener;
    //let a higher class tune in when the quiz is done
    public void Listen(IQuizDone listener){
        this.listener = listener;


    }

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

    //return the question for the specific index
    public static String GetQText(int i) {


        return question[i];
    }
    //get from firebase the path to the question tree based on userid, needed to access and store question answers
    public static String getUserQuestionPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            String userId = user.getUid();  // This is the user ID
            return "Users/" + userId ;



    }


    //We have three question sections, loads the last and first section and loads the middle based on the firsst question
    protected void LoadSection(int i, boolean loadFirst) {
    //load first indicates whether we are loading from left to right or right to left (am i going to a previous section?)
        final int[] k = {0};


        if (i > 2 || i == -1) { // call quiz over if last section done
            if(i>2) {

                listener.QuizDone();
            }

        } else {
            switch (i) {
                case 0:
                    //if we are loading the first section, load the warmup path
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
                    //if we are loading the second section, check the first answer and load the corresponding section

                    Log.d("chu", "guh");

                    DatabaseReference dbRef = db.getReference(getUserQuestionPath());
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.child("Q&A").child("WarmUp").child("" + 1).child("" + 1).getChildren()) {
                                String choice = child.getValue(String.class);
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
                                    Log.d("chu", "h");

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
                    // load the last section
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

    //Simple initialization for fragment state adapter
    protected void loadPagerAdapter(int length) {
        fsa = new QuestionFSA(getActivity(), this, length);
        questionPager.setAdapter(null);

        questionPager.setAdapter(fsa);
    }
    //On attatchment to parrent activity, initialize question list from the JSON definition based in the assets folder
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadQuestionsFromAssets(context);
    }
    //Create the tree for a user that doesn't have a tree for storing answers :(
    void createTree() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference sourceRef = db.getReference("Q&A");
        DatabaseReference destinationRef = db.getReference(getUserQuestionPath());

        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object data = snapshot.getValue();

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("Q&A", data);  // This puts q/a tree into the destination

                    destinationRef.updateChildren(updateMap, (error, ref) -> {
                        if (error == null) {
                        } else {
                            Log.d("Gupper", "chuppy no");
                        }
                    });
                } else {
                    Log.w("Gupper", "silly goose");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Gyooer", "cancelled");
            }
        });
    }

    //Initialization

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadQuestionsFromAssets(getContext());

        // Log.d("gupper", "created");
        db = FirebaseDatabase.getInstance();
        createTree();
        View view = inflater.inflate(R.layout.question_activity, container, false);
        questionPager = (ViewPager2) view.findViewById(R.id.QVP2);
        next = view.findViewById(R.id.QuestionNext);
        prev = view.findViewById(R.id.QuestionPrevious);

        questionPager.setUserInputEnabled(false); //disable swiping for the fragment state adapter, for the next previous buttons
        loadPagerAdapter(warmupOptions.length);
        question = getResources().getStringArray(R.array.warmup_array);


        //Initialize next and previus buttons. Basically on next we set variables to the next question, unless its the last question, then we load the next section
        next.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {
                int i = questionPager.getCurrentItem();
                ArrayList<String> answers = GetAnswer();
                if (answers == null || answers.isEmpty()) {
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
        return view;
    }

    //Store answers based on current path
    protected void StoreAnswer(int i, ArrayList<String> answers) {

        DatabaseReference dbRef = db.getReference(getUserQuestionPath());
        dbRef.child("Q&A").child(path).child("" + (i + 1)).child("" + 1).push().setValue(answers.get(0));
        for (int j = 1; j < answers.size(); j++) {
            dbRef.child("" + (i + 1)).child("" + 2).push().setValue(answers.get(j));
        }
    }
//Loas the corresponding answer based on the question index
    protected void LoadAnswer(int i) {

        DatabaseReference dbRef = db.getReference(getUserQuestionPath());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.child("Q&A").child(path).child("" + (i + 1)).child("" + 1).getChildren()) {
                    child.getRef().removeValue();
                }
                for (DataSnapshot child : snapshot.child("Q&A").child(path).child("" + (i + 1)).child("" + 2).getChildren()) {
                    child.getRef().removeValue();
                }

                int answerType = snapshot.child("Q&A").child(path).child("" + (i + 1)).child("" + 0).getValue(Integer.class);
                // Log.d("guh", "" + (answerType));
                // Log.d("guh", "" + (i+1));
                //Grab the question type from the firebase datatype and create the corresponding fragment
                // this is a terrible practice but im too far deep in
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
                getChildFragmentManager().beginTransaction().replace(R.id.AnswerContainer, currentAnswerFrag).commit();

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
    // parse the json file and load the questions into my arrays, stored in a class called section
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
    //store into a section class based on the given json root
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


