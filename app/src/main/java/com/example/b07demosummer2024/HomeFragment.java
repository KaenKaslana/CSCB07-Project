package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        Button buttonQuestionnaire = view.findViewById(R.id.buttonQuestionnaire);
        Button buttonCategory = view.findViewById(R.id.buttonCategory);
        Button buttonReminder = view.findViewById(R.id.buttonReminder);
        Button buttonSupportConnection = view.findViewById(R.id.buttonSupportConnection);

        buttonQuestionnaire.setOnClickListener(v ->
                loadFragment(new QuestionView())
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

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
