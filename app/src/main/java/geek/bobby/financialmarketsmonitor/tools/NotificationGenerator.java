package geek.bobby.financialmarketsmonitor.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import geek.bobby.financialmarketsmonitor.R;

public class NotificationGenerator {

    @NonNull final String channelID;
    @NonNull final Context context;
    final int importance;

    public NotificationGenerator(@NonNull Context context,
            @NonNull String channelID,
                                 @NonNull String channelName,
                                 @Nullable String channelDescription,
                                 int importance)
    {
        this.context = context;
        this.channelID = channelID;
        this.importance = importance;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            if (channelDescription!=null)
                channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public Notification create(@NonNull String title,
                               @NonNull String body,
                               long when,
                               @Nullable Intent intent,
                               boolean isOnGoing)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setContentTitle(title)
                .setContentText(body)
                .setOngoing(isOnGoing)
                .setSmallIcon(R.drawable.ic_timeline_notification)
                .setWhen(when);

        if (importance >= NotificationManagerCompat.IMPORTANCE_DEFAULT)
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        if (body.length()>30)
            builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(body));

        if (intent!=null)
            builder.setContentIntent(PendingIntent.getActivity(context,0,intent,
                    PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(!isOnGoing);

        return builder.build();
    }

}
