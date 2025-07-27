package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CreateSchedule extends AppCompatActivity {

    private DatabaseReference remindersRef;
    private RadioGroup button;
    private EditText time;
    private Spinner day;
    private Spinner date;
    private Button save;
    private String frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_schedule);


        // Replace your current initialization with this:
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cscb07-group-2-default-rtdb.firebaseio.com");
        remindersRef = database.getReference("users/user1/reminder");

        button = findViewById(R.id.choose);
        time = findViewById(R.id.timePick);
        day = findViewById(R.id.dayPicker);
        date = findViewById(R.id.datePicker);
        save = findViewById(R.id.save);

        button.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        day.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        //set them all invisible because what is visible depends on mode your in

        Spinner spinnerDay = (Spinner) day;

        ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(
                this,
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );

        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDay);
        //spinner with days of week(Mon-Sun)

        Spinner spinnerDate = (Spinner) date;

        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(
                this,
                R.array.monthly_dates,
                android.R.layout.simple_spinner_item
        );

        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapterDate);
        //spinner with dates (1-31)

        //if editing mode (you should be just prompted to change time, day/date (depending on whether your changing a daily, weekly, monthly reminder)
        boolean isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        Reminder existingReminder = (Reminder) getIntent().getSerializableExtra("REMINDER_DATA");
        String reminderKey = getIntent().getStringExtra("REMINDER_KEY");
        if (isEditMode && existingReminder != null) {
            // EDIT MODE: Hide radio buttons, show only relevant fields
            button.setVisibility(View.GONE);
            frequency = existingReminder.getFrequency();
            time.setVisibility(View.VISIBLE);
            time.setText(existingReminder.getTime());

            if ("weekly".equals(frequency)) {
                day.setVisibility(View.VISIBLE);
                // Set the spinner to the saved day
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) day.getAdapter();
                int position = adapter.getPosition(existingReminder.getDay());
                day.setSelection(position);
            }
            else if ("monthly".equals(frequency)) {
                date.setVisibility(View.VISIBLE);
                date.setSelection(existingReminder.getDate() - 1);
            }

            save.setVisibility(View.VISIBLE);
        } else {
            // CREATE MODE: Show radio buttons and handle clicks, you choose everything
            button.setVisibility(View.VISIBLE);
            button.setOnCheckedChangeListener((group, checkedId) -> {
                time.setVisibility(View.VISIBLE);
                if (checkedId == R.id.daily) {
                    frequency = "daily";
                    day.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                } else if (checkedId == R.id.weekly) {
                    frequency = "weekly";
                    day.setVisibility(View.VISIBLE);
                    date.setVisibility(View.GONE);
                } else if (checkedId == R.id.monthly) {
                    frequency = "monthly";
                    day.setVisibility(View.GONE);
                    date.setVisibility(View.VISIBLE);
                }
                save.setVisibility(View.VISIBLE);
            });
        }


        save.setOnClickListener(new View.OnClickListener() {
            //once save button is clicked, save the info of reminder to the firebase and set up notif with work manager
            @Override
            public void onClick(View v) {
                if (frequency == null || frequency.isEmpty()) {
                    Toast.makeText(CreateSchedule.this,
                            "Please select a reminder frequency first",
                            Toast.LENGTH_SHORT).show();
                    return;
                }//cannot proceed without choosing frequency

                int[] timing = validateTimeInput(time);
                if (timing == null) return;
                int hour = timing[0];
                int minute = timing[1];

                String timeString = String.format("%02d:%02d", hour, minute);
                String key;
                if (isEditMode) {
                     key = reminderKey; // Update existing child node
                } else {
                    key = remindersRef.push().getKey(); // Create new child node
                }


                if(frequency.equals("daily")) {
                    //saving to firebase
                    Reminder reminder = new Reminder("daily", timeString);
                    remindersRef.child(key).setValue(reminder).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateSchedule.this, "Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateSchedule.this, "Save failed (complete)", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //notification code, first find the current hour and minute, then use initial delay to find first notif time, then repeat every 24 hours from that time
                    Calendar now = Calendar.getInstance();
                    Calendar targetTime = (Calendar) now.clone();
                    targetTime.set(Calendar.HOUR_OF_DAY, hour);
                    targetTime.set(Calendar.MINUTE, minute);
                    targetTime.set(Calendar.SECOND, 0);

                    if (targetTime.before(now)) {
                        targetTime.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    long initialDelay = targetTime.getTimeInMillis() - now.getTimeInMillis();

                    PeriodicWorkRequest dailyRequest = new PeriodicWorkRequest.Builder(
                            ReminderWorker.class,
                            24,//repeat every 24 hours
                            TimeUnit.HOURS
                    ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)//initial delay before you start repeating every 24 hours
                    .build();
                    WorkManager.getInstance(CreateSchedule.this).enqueue(dailyRequest);
                    finish();

                } else if (frequency.equals("weekly")) {
                    //saving
                    String selectedDay = day.getSelectedItem().toString();
                    Reminder reminder = new Reminder("weekly", timeString,selectedDay);
                    remindersRef.child(key).setValue(reminder).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateSchedule.this, "Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateSchedule.this, "Save failed (complete)", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //notification code, this time find current day, set the time and find delay, then repeat every 7 days
                    int selectedPosition = day.getSelectedItemPosition();
                    int calendarDay = selectedPosition + 2;
                    Calendar now = Calendar.getInstance();
                    Calendar targetDay = (Calendar) now.clone();
                    targetDay.set(Calendar.HOUR_OF_DAY, hour);
                    targetDay.set(Calendar.MINUTE, minute);
                    targetDay.set(Calendar.SECOND, 0);
                    targetDay.set(Calendar.DAY_OF_WEEK, calendarDay);
                    if (targetDay.before(now)) {
                        targetDay.add(Calendar.DAY_OF_WEEK, 7);
                    }
                    long initialDelay = targetDay.getTimeInMillis() - now.getTimeInMillis();

                    PeriodicWorkRequest weeklyRequest = new PeriodicWorkRequest.Builder(
                            ReminderWorker.class,
                            7,
                            TimeUnit.DAYS
                    ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                            .build();
                    WorkManager.getInstance(CreateSchedule.this).enqueue(weeklyRequest);
                    finish();

                } else if (frequency.equals("monthly")) {
                    int selectedDate = date.getSelectedItemPosition() + 1;
                    Reminder reminder = new Reminder("monthly", timeString,selectedDate);
                    remindersRef.child(key).setValue(reminder).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateSchedule.this, "Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateSchedule.this, "Save failed (complete)", Toast.LENGTH_SHORT).show();
                        }
                    });



                    //notification code, this ones different, make a one time request, and each time you build a new notif, make another one time request based on whether month carries the date
                    Calendar now = Calendar.getInstance();
                    Calendar targetDate= (Calendar) now.clone();
                    targetDate.set(Calendar.HOUR_OF_DAY, hour);
                    targetDate.set(Calendar.MINUTE, minute);
                    targetDate.set(Calendar.SECOND, 0);
                    Data inputData = new Data.Builder()
                            .putInt("DAY_OF_MONTH", selectedDate)
                            .putInt("HOUR", hour)
                            .putInt("MINUTE", minute)
                            .build();

                    int maxDay = targetDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                    targetDate.set(Calendar.DAY_OF_MONTH, Math.min(selectedDate, maxDay));
                    if (targetDate.before(now)) {
                        targetDate.add(Calendar.MONTH, 1);
                        maxDay = targetDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                        targetDate.set(Calendar.DAY_OF_MONTH, Math.min(selectedDate, maxDay));
                    }
                    long initialDelay = targetDate.getTimeInMillis() - now.getTimeInMillis();

                    OneTimeWorkRequest monthlyRequest = new OneTimeWorkRequest.Builder(
                            ReminderWorkerMonthly.class)
                            .setInputData(inputData)
                            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                            .build();
                    WorkManager.getInstance(CreateSchedule.this).enqueue(monthlyRequest);
                    finish();
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private int[] validateTimeInput(EditText time) {
        String input = time.getText().toString().trim();

        // finds whether time entered is valid
        if (!input.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            Toast.makeText(this, "Invalid time! Use HH:MM (e.g. 14:30)",
                    Toast.LENGTH_SHORT).show();
            return null;
        }


        String[] parts = input.split(":");
        return new int[]{
                Integer.parseInt(parts[0]), // hour
                Integer.parseInt(parts[1])  // minute
        };
    }

}