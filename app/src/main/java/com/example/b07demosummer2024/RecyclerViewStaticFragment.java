package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 *
 *Bryce Chen
 *
 *Fragment for displaying tips
 *
 *
 */

public class RecyclerViewStaticFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    String mid;
    FirebaseDatabase db;
    //initialization nearly identical to the orignal recycler view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_static, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
       ;

        db = FirebaseDatabase.getInstance();
        InitSub();
        LoadAll();

        return view;
    }

    //Create the recycler view basically
    void CreateList() {

        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

    }

    //Load the first and last section, and the middle section based on the section

    void LoadAll() {
        LoadTips("WarmUp", QuestionView.warmupSection.tips);
       GetFirstPath();
   // LoadTips("Still", QuestionView.stillSection.tips);

       LoadTips("Follow", QuestionView.followSection.tips);



    }

    void GetFirstPath() {
        //get teh answer from the first question, and then get the corresponding section's answers
        DatabaseReference dbRef = db.getReference(QuestionView.getUserQuestionPath()+"Q&A/WarmUp");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iter = snapshot.child("" + 1).child("" + 1).getChildren();
                String choice = iter.iterator().next().getValue(String.class);
                ArrayList<Hashtable<String,String>> arr = null;
                if(choice.equals("Still in a relationship")) {

                    mid = "Still";
                    arr = QuestionView.stillSection.tips;
                } else if (choice.equals("Planning to leave")) {mid = "Planning";arr = QuestionView.planSection.tips;}
                else{ mid = "Post"; arr = QuestionView.postSection.tips;}
                LoadTips(mid, arr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    Hashtable<String, Integer> subSet;
    //0 is use as answer, 1 is subanswer, 2 is sequence in commas, 3 is follow up sepcial (0 as ttemp),4 is live with
    // this initilization for tips with special formats, see above
    private void InitSub() {
        subSet = new Hashtable<String, Integer>();
        subSet.put(QuestionView.WarmUpPath + 1, 0);
        subSet.put(QuestionView.WarmUpPath + 2, 0);
        subSet.put(QuestionView.WarmUpPath + 3, 0);
        subSet.put(QuestionView.WarmUpPath + 4, 4);
        subSet.put(QuestionView.WarmUpPath + 5, 1);
        subSet.put(QuestionView.StillPath + 1, 2);
        subSet.put(QuestionView.StillPath + 3, 0);
        subSet.put(QuestionView.PlanningPath + 1, 0);
        subSet.put(QuestionView.PlanningPath + 3, 0);
        subSet.put(QuestionView.PlanningPath + 4, 1);
        subSet.put(QuestionView.PostPath + 2, 1);
        subSet.put(QuestionView.PostPath + 3,1 );
        subSet.put(QuestionView.FollowUpPath + 1,3 );


    }
    String safe;
    //handlign replacing teh "answer" bracket based on the tip display type
    private String HandleSubAnswer(String tip, int index, String sub, String answer, Iterable<DataSnapshot> iter) {
        String res= null;
        String ans = "{answer}";
        if(sub.equals(QuestionView.WarmUpPath) && index == 3) {
            safe = answer;
        }
        int toDo = subSet.get(sub+index);
        if(toDo == 0) {
            return tip.replace(ans, answer);
        }
        else if(toDo==1) {
            return tip.replace(ans, iter.iterator().next().getValue(String.class));
        }
        else if(toDo ==2) {
            String str = new String(answer);
            while(iter.iterator().hasNext()) {
                str.concat(", " + iter.iterator().next().getValue(String.class));
            }
            return tip.replace(ans, str);
        }else if (toDo == 3) {
            return tip.replace(ans, answer);
        }else if (toDo == 4)
        {
        if(answer.equals("Partner")) {  return tip.replace(ans,safe); }
        return tip.replace(ans, answer);
        }

        return null;
    }

    //Get answers from database and then create the list
    private void LoadTips(String sub, ArrayList<Hashtable<String,String>> arr) {
        DatabaseReference dbRef = db.getReference(QuestionView.getUserQuestionPath()+"Q&A/" + sub);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int i =1;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iter = snapshot.child("" + i).child("" + 1).getChildren();
                while(iter.iterator().hasNext()){
                    Log.d("bup","sup" + i);
                 DataSnapshot child = iter.iterator().next();
                    String answer = child.getValue(String.class);


                String tip = arr.get(i-1).get(answer);
                if(tip == null) {tip = arr.get(i-1).get("");}
                if(tip.contains("{answer}")) {

                    Iterable<DataSnapshot> iter2 = snapshot.child("" + i).child("" + 2).getChildren();

                    tip =  HandleSubAnswer(tip, i,sub,answer, iter2);
                }

                itemList.add(new Item("Item"+ i, "Tip " +i , tip));
                    Log.d("bup",answer);
                    Log.d("bup",tip);


                    i++;
                    iter = snapshot.child("" + i).child("" + 1).getChildren();

                }
                CreateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


}
