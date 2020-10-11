package geek.bobby.financialmarketsmonitor.messaging;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;
import geek.bobby.financialmarketsmonitor.messaging.models.SymbolRate;

public class MessagingController implements MessagingContract.IMessagingPresenter
{

    private final ObjectMapper objectMapper;
    private MessagingContract.IMessageView view;

    public MessagingController(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void setView(@NonNull MessagingContract.IMessageView view) {
        this.view = view;
    }

    @Override
    public void handleMessage(@NonNull String type, @NonNull String rawJson) throws JsonProcessingException {
        if (view==null)
            return;

        switch (type.toLowerCase())
        {
            case MessagingContract.TypeSymbolRate:
                SymbolRate symbolRate = objectMapper.readValue(rawJson, SymbolRate.class);
                view.displaySymbolRate(symbolRate);
                break;
        }
    }

    @Override
    public void handlePlainTextMessage(@NonNull PlainTextMessage plainTextMessage) {
        view.displayPlanTextMessage(plainTextMessage);
    }
}
