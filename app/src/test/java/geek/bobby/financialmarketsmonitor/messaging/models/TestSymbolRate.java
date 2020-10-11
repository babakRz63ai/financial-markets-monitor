package geek.bobby.financialmarketsmonitor.messaging.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import geek.bobby.financialmarketsmonitor.SerializingTester;

public class TestSymbolRate {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
    }

    @Test
    public void testSerializing() throws IOException, ClassNotFoundException {
        SymbolRate symbolRate = new SymbolRate("USD/EUR", 0.876f, new Date());
        SerializingTester.test(symbolRate);
    }

    @Test
    public void testParsingJson() throws JsonProcessingException {
        final String aSymbol = "EUR/USD";
        final float aRate = 0.876f;
        final Date timeStamp = new Date();
        final String jsonObject = String.format(Locale.ROOT,
                "{\"Symbol\":\"%s\",\"Rate\":%f,\"TimeStamp\":%d}",
                 aSymbol, aRate, timeStamp.getTime());

        SymbolRate symbolRate = (SymbolRate) objectMapper.readValue(jsonObject, SymbolRate.class);
        assertEquals(aSymbol, symbolRate.getSymbol());
        assertEquals(aRate, symbolRate.getRate(),0.001);
        assertEquals(timeStamp, symbolRate.getTimeStamp());
    }
}
