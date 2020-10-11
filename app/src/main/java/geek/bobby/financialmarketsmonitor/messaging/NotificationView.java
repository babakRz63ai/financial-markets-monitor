package geek.bobby.financialmarketsmonitor.messaging;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import geek.bobby.financialmarketsmonitor.MainActivity;
import geek.bobby.financialmarketsmonitor.R;
import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;
import geek.bobby.financialmarketsmonitor.messaging.models.SymbolRate;
import geek.bobby.financialmarketsmonitor.tools.NotificationGenerator;

public class NotificationView implements MessagingContract.IMessageView
{
    @NonNull Context context;
    @NonNull private final NotificationManagerCompat notificationManager;
    @NonNull private final LocalBroadcastManager localBroadcastManager;
    @NonNull private final NotificationGenerator notificationGenerator;
    @NonNull private final String defaultTitle;

    public NotificationView(@NonNull Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        notificationGenerator = new NotificationGenerator(context,
                context.getString(R.string.normal_notifications_channel_ID),
                context.getString(R.string.normal_notifications_channel_name),
                null,
                NotificationManagerCompat.IMPORTANCE_DEFAULT);
        defaultTitle = context.getString(R.string.app_name);
    }

    @Override
    public void displaySymbolRate(@NonNull SymbolRate symbolRate) {
        dispatchMessage(String.format(context.getString(R.string.format_symbol),symbolRate.getSymbol()),
                String.format(context.getString(R.string.format_rate), symbolRate.getRate()),
                symbolRate.getTimeStamp().getTime(),
                new Intent(context, MainActivity.class)
                    .setAction(MessagingContract.BroadcastActions.SymbolAndRate)
                    .putExtra(MessagingContract.ExtraSymbolRate, symbolRate));
    }

    @Override
    public void displayPlanTextMessage(@NonNull PlainTextMessage plainTextMessage) {
        dispatchMessage(plainTextMessage.getTitle()!=null ? plainTextMessage.getTitle() : defaultTitle,
                plainTextMessage.getBody(),
                System.currentTimeMillis(),
                new Intent(context, MainActivity.class)
                .setAction(MessagingContract.BroadcastActions.PlainTextMessage)
                .putExtra(MessagingContract.ExtraPlainTextMessage, plainTextMessage));
    }

    private void dispatchMessage(@NonNull String title,
                                 @NonNull String body,
                                 long timeStampMS,
                                 @NonNull Intent intent)
    {
        /*if (!localBroadcastManager.sendBroadcast(some intent))
            use notificationManager to display a notification containing the same intent
         */
        if (!localBroadcastManager.sendBroadcast(intent))
            notificationManager.notify((int)System.currentTimeMillis(),
                    notificationGenerator.create(title, body, timeStampMS, intent, false));
    }
}
