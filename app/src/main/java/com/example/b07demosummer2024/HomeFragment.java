package com.example.b07demosummer2024;

import android.content.Context;
import android.content.Intent;
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
    private QuestionView quiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.activity_home_fragment,
                container,
                false
        );

        //SharedPreferences to track quiz completion
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean doneQuiz = prefs.getBoolean("DoneQuiz", false);

        //Bind UI buttons
        Button buttonQuestionnaire     = view.findViewById(R.id.buttonQuestionnaire);
        Button buttonCategory          = view.findViewById(R.id.buttonCategory);
        Button buttonReminder          = view.findViewById(R.id.buttonReminder);
        Button buttonSupportConnection = view.findViewById(R.id.buttonSupportConnection);
        Button tipsButton              = view.findViewById(R.id.Tips);
        Button logoutButton            = view.findViewById(R.id.logoutButton);

        //Tips screen
        QuestionView.loadQuestionsFromAssets(getContext());
        tipsButton.setOnClickListener(v ->
                loadFragment(new RecyclerViewStaticFragment())
        );

        //Questionnaire button
        buttonQuestionnaire.setOnClickListener(v -> {
            quiz = new QuestionView();
            quiz.Listen(this);

            // reset flag so quiz shows again if needed
            prefs.edit()
                    .putBoolean("DoneQuiz", false)
                    .apply();

            loadFragment(quiz);
        });

        //Other navigation buttons
        buttonCategory.setOnClickListener(v ->
                loadFragment(new CategoryFragment())
        );
        buttonReminder.setOnClickListener(v ->
                loadFragment(new RemindersFragment())
        );
        buttonSupportConnection.setOnClickListener(v ->
                loadFragment(new SupportConnectionFragment())
        );

        //Auto-launch quiz on first visit
        if (!doneQuiz) {
            quiz = new QuestionView();
            quiz.Listen(this);
            loadFragment(quiz);
        }

        //LOGOUT button
        logoutButton.setOnClickListener(v -> {
            // Optional: clear auth state here, e.g. FirebaseAuth.getInstance().signOut();

            // Go back to MainActivity as a fresh task
            Intent intent = new Intent(requireActivity(), MainActivity.class)
                    .addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
            startActivity(intent);
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void QuizDone() {
        Log.d("quiz", "Quiz completed!");
        requireActivity().getSupportFragmentManager().popBackStack();

        // Mark quiz done
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean("DoneQuiz", true)
                .apply();
    }
}
