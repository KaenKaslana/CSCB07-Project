package com.example.b07demosummer2024;


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

public class ReminderWorker extends Worker {
    public ReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        //this is for the daily and weekly, doWork() just build and send the notif
        //when opening the notif, take you to mainactivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //builds the notification details
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)//the notification icon
                .setContentTitle("Review Reminders")
                .setContentText("Its time to review your plan.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);//takes te mainactivity

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success();
        }
        manager.notify((int) System.currentTimeMillis(), builder.build());//send the notifciation

        return Result.success();


    }
}