package geek.bobby.financialmarketsmonitor.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;

import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;
import geek.bobby.financialmarketsmonitor.messaging.models.SymbolRate;

public interface MessagingContract {

    // Possible model types
    String TypeSymbolRate = "symbolrate";

    // Actions for broadcasts
    interface BroadcastActions
    {
        String PlainTextMessage = "MessagingContract.BroadcastActions.PlainTextMessage";
        String SymbolAndRate = "MessagingContract.BroadcastActions.SymbolAndRate";
    }

    // An instance of SymbolRate (serializable)
    String ExtraSymbolRate = "MessagingContract.ExtraSymbolRate";

    // An instance of PlainTextMessage (serializable)
    String ExtraPlainTextMessage = "MessagingContract.ExtraPlainTextMessage";

    //-------------------------------- An abstract view

    interface IMessageView
    {
        void displaySymbolRate(@NonNull SymbolRate symbolRate);

        void displayPlanTextMessage(@NonNull PlainTextMessage plainTextMessage);
    }

    //-------------------------------- An abstract presenter

    interface IMessagingPresenter
    {
        void setView(@NonNull IMessageView view);

        void handleMessage(@NonNull String type,
                           @NonNull String rawJson) throws JsonProcessingException;

        void handlePlainTextMessage(@NonNull PlainTextMessage plainTextMessage);
    }

}
