package com.example.b07demosummer2024 ;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
/**
 * Worker class for handling daily and weekly reminder notifications.
 * Extends {@link Worker} to perform background work when triggered by WorkManager.
 * This class builds and displays notifications when the scheduled reminder time arrives.
 */
public class ReminderWorker extends Worker {
    /**
     * Creates a new instance of ReminderWorker.
     * @param context The application context
     * @param params Worker parameters
     */
    public ReminderWorker(
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
        // Create intent that will open LoginActivity when notification is tapped
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //builds the notification details
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)//the notification icon
                .setContentTitle("Review Reminders")
                .setContentText("Its time to review your plan.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);//takes to login

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success();
        }
        manager.notify((int) System.currentTimeMillis(), builder.build());//send the notification

        return Result.success();


    }
}