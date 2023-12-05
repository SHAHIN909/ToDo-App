package com.example.todo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MemoBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeating_Intent = new Intent(context,NotificationOpen.class);
        repeating_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);



         PendingIntent pendingIntent = PendingIntent.getActivity
                    (context, 0, repeating_Intent, PendingIntent.FLAG_MUTABLE);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Notification")
                .setContentText("Hey check the toDoList!!")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);



        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
             return;
        }
        notificationManager.notify(123, builder.build());


    }






}