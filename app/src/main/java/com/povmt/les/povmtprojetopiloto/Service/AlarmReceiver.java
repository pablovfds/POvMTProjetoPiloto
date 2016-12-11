package com.povmt.les.povmtprojetopiloto.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Activities.HomeActivity;

public class AlarmReceiver extends BroadcastReceiver {

    MediaPlayer notificationSound;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("on receive", "receiver");
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        addNotification(context);

    }

    private void addNotification(Context context) {

        notificationSound = MediaPlayer.create(context, R.raw.notification);
        notificationSound.start();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("POvMT")
                        .setContentText("Você não investiu tempo em suas atividades ontem! ")
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}