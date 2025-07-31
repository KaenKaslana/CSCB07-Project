package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Reminders extends AppCompatActivity {
    protected RadioGroup button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminders);
        button = findViewById(R.id.options); //radio button

        //if click add go to createschedule, if click editdelete go to changereminderactvity

        button.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.add) {
                startActivity(new Intent(Reminders.this, CreateSchedule.class));
            } else if (checkedId == R.id.editdelete) {
                startActivity(new Intent(Reminders.this, ChangeReminderActivity.class));
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}