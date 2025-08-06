package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment implements IQuizDone {
    QuestionView quiz;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        boolean DoneQuiz = prefs.getBoolean("DoneQuiz", false);

        Button buttonQuestionnaire = view.findViewById(R.id.buttonQuestionnaire);
        Button buttonCategory = view.findViewById(R.id.buttonCategory);
        Button buttonReminder = view.findViewById(R.id.buttonReminder);
        Button buttonSupportConnection = view.findViewById(R.id.buttonSupportConnection);
        Button tipsButton = view.findViewById(R.id.Tips);
        QuestionView.loadQuestionsFromAssets(getContext());
        tipsButton.setOnClickListener(v->
                loadFragment(new RecyclerViewStaticFragment())
        );

        buttonQuestionnaire.setOnClickListener(v -> {
                quiz = new QuestionView();
                quiz.Listen(this);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DoneQuiz", false);
            editor.apply();

                loadFragment(quiz); }
        );

        buttonCategory.setOnClickListener(v ->
                loadFragment(new CategoryFragment())
        );
        buttonReminder.setOnClickListener(v ->
                loadFragment(new RemindersFragment())
        );
        buttonSupportConnection.setOnClickListener(v ->
                loadFragment(new SupportConnectionFragment())
        );


        if(!DoneQuiz) {
            //  SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            quiz = new QuestionView();
            quiz.Listen(this);
            loadFragment(quiz);

        }

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void QuizDone() {
        Log.d("quiz", "hello");
        requireActivity().getSupportFragmentManager().popBackStack();
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("DoneQuiz", true);
        editor.apply();
    }
}
