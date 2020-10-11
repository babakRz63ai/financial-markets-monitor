package geek.bobby.financialmarketsmonitor.messaging.models;

import org.junit.Test;

import java.io.IOException;

import geek.bobby.financialmarketsmonitor.SerializingTester;

public class TestPlainTextMessage {
    @Test
    public void testSerializing() throws IOException, ClassNotFoundException {
        SerializingTester.test(new PlainTextMessage("Yankee","Catch the cow!"));
        SerializingTester.test(new PlainTextMessage(null,"A message without body"));
        SerializingTester.test(new PlainTextMessage("عنوان", "سلام چطوری؟"));
    }
}
