package com.example.b07demosummer2024;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
/**
 * Worker class for handling daily and weekly reminder notifications.
 * Extends {@link Worker} to perform background work when triggered by WorkManager.
 * This class builds and displays notifications when the scheduled reminder time arrives.
 */
public class ReminderWorkerMonthly extends Worker {
    /**
     * Creates a new instance of ReminderWorker.
     * @param context The application context
     * @param params Worker parameters
     */
    public ReminderWorkerMonthly(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }
    /**
     * Called by WorkManager when it's time to perform the scheduled work.
     * Builds and displays a notification, then returns a success result.
     * @return The result of the work (success)
     */
    @Override
    public Result doWork() {
        //since monthly dates do not repeat on by constant interval, keep making oneTimeRequests and check whether date exist in month
        Data inputData = getInputData();
        int dayOfMonth = inputData.getInt("DAY_OF_MONTH", 1);
        int hour = inputData.getInt("HOUR", 12);
        int minute = inputData.getInt("MINUTE", 0);

        // Create intent that will open LoginActivity when notification is tapped
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //building notification details
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)//the notification icon
                .setContentTitle("Review Reminders")
                .setContentText("Its time to review your plan.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);//taken to main activity

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success();
        }
        manager.notify((int) System.currentTimeMillis(), builder.build());//send the notification, makes it visible

        //checking a month from now that date
        Calendar nextDate = Calendar.getInstance();
        nextDate.add(Calendar.MONTH, 1);
        nextDate.set(Calendar.HOUR_OF_DAY, hour);
        nextDate.set(Calendar.MINUTE, minute);
        nextDate.set(Calendar.SECOND, 0);
        //find max day of next month (say you choose 31st as your date but feb has max 28 days, we stick with max day)
        int maxDay = nextDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        nextDate.set(Calendar.DAY_OF_MONTH, Math.min(dayOfMonth, maxDay));//choose the min of the max day of month or date chosen

        //delay until that day, then set up another one time request
        long delay = nextDate.getTimeInMillis() - System.currentTimeMillis();
        OneTimeWorkRequest nextRequest = new OneTimeWorkRequest.Builder(
                ReminderWorkerMonthly.class
        ).setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(nextRequest);
        return Result.success();
    }
}