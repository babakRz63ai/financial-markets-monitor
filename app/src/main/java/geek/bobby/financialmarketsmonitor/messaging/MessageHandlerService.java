package geek.bobby.financialmarketsmonitor.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;

public class MessageHandlerService extends JobIntentService
{

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
    }

    // String, raw form of a model in JSON
    static final String ExtraMessageModel = "ExtraModel";

    // String, A model identifier
    static final String ExtraMessageType = "ExtraType";

    // An instance of PlainTextMessage
    static final String ExtraPlainTextMessage = "ExtraPlainTextMessage";

    private MessagingController messagingController;

    @Override
    public void onCreate() {
        super.onCreate();
        messagingController = new MessagingController(objectMapper);
        NotificationView notificationView = new NotificationView(this);
        messagingController.setView(notificationView);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        if (intent.hasExtra(ExtraMessageType)) {
            final String type = intent.getStringExtra(ExtraMessageType);
            final String model = intent.getStringExtra(ExtraMessageModel);
            if (type != null && model != null) {
                handleMessageData(type, model);
            }
        }

        if (intent.hasExtra(ExtraPlainTextMessage))
        {
            PlainTextMessage plainTextMessage = (PlainTextMessage)intent.getSerializableExtra(ExtraPlainTextMessage);
            if (plainTextMessage!=null)
                messagingController.handlePlainTextMessage(plainTextMessage);
        }
    }

    private void handleMessageData(@NonNull String type, @NonNull String model)
    {
        Log.i("MessageHandlerService","handleMessageData called by type="+type+
                " , model = "+model);
        try {
            messagingController.handleMessage(type,model);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}
