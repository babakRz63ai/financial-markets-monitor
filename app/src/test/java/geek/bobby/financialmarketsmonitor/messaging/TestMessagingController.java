package geek.bobby.financialmarketsmonitor.messaging;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.Locale;

import geek.bobby.financialmarketsmonitor.messaging.models.PlainTextMessage;
import geek.bobby.financialmarketsmonitor.messaging.models.SymbolRate;

public class TestMessagingController
{
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
    }

    @Test
    public void testHandleMessage() throws JsonProcessingException {
        final SymbolRate symbolRate = new SymbolRate("EUR/JPY",23.45f, new Date());
        final MessagingContract.IMessageView view = mock(MessagingContract.IMessageView.class);
        final String jsonObject = String.format(Locale.ROOT,
                "{\"Symbol\":\"%s\",\"Rate\":%f,\"TimeStamp\":%d}",
                symbolRate.getSymbol(), symbolRate.getRate(), symbolRate.getTimeStamp().getTime());

        MessagingController controller = new MessagingController(objectMapper);
        controller.setView(view);
        controller.handleMessage(MessagingContract.TypeSymbolRate, jsonObject);
        verify(view).displaySymbolRate(symbolRate);
    }

    @Test
    public void testHandlePlainTextMessage()
    {
        final PlainTextMessage plainTextMessage = new PlainTextMessage("Yankee","Catch the cow!");
        final MessagingContract.IMessageView view = mock(MessagingContract.IMessageView.class);

        MessagingController controller = new MessagingController(objectMapper);
        controller.setView(view);
        controller.handlePlainTextMessage(plainTextMessage);
        verify(view).displayPlanTextMessage(plainTextMessage);
    }
}
