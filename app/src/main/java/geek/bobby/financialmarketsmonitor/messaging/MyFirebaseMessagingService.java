package geek.bobby.financialmarketsmonitor.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("MyFirebaseMessage","onMessageReceived");

        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("type") &&
                    remoteMessage.getData().containsKey("model"))
                // It is a data massage. We expect this form:
                // {"type":"some type", "model":"{Some model}"}
                MessageHandlerService.enqueueWork(this, MessageHandlerService.class,
                        0,
                        new Intent()
                                .putExtra(MessageHandlerService.ExtraMessageType,
                                        remoteMessage.getData().get("type"))
                                .putExtra(MessageHandlerService.ExtraMessageModel,
                                        remoteMessage.getData().get("model")));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {
            if (remoteMessage.getNotification().getBody()!=null)
                MessageHandlerService.enqueueWork(this, MessageHandlerService.class,
                    0,
                    new Intent()
                        .putExtra(MessageHandlerService.ExtraPlainTextMessage,
                                new PlainTextMessage(remoteMessage.getNotification().getTitle(),
                                        remoteMessage.getNotification().getBody())));
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        startService(new Intent(this, FCMRegistrationService.class)
            .setAction(FCMRegistrationService.ActionRetrieveAndSubmitRegistrationId));
    }
}
