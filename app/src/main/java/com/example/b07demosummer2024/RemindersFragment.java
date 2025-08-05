package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.activity.EdgeToEdge;

public class RemindersFragment extends Fragment {
    private RadioGroup optionsGroup;

    public RemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Enable edge-to-edge if you need it
        EdgeToEdge.enable(requireActivity());
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        optionsGroup = view.findViewById(R.id.options);
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.add) {
                startActivity(new Intent(getActivity(), CreateSchedule.class));
            } else if (checkedId == R.id.editdelete) {
                startActivity(new Intent(getActivity(), ChangeReminderActivity.class));
            }
        });

        // handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }
}
