package com.atexpose.dispatcher.logging.format;

import com.atexpose.dispatcher.logging.LogKey;
import io.schinzel.basicutils.FunnyChars;
import org.json.JSONObject;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Schinzel
 */
public class JsonFormatterTest {
    


    @Test
    public void testFormatLogEntry() {
        Map<String, String> logdata = new LinkedHashMap<>();
        logdata.put(LogKey.SENDER.toString(), "1234");
        logdata.put(LogKey.REQUEST.toString(), FunnyChars.CYRILLIC_LETTERS.getString());
        logdata.put(LogKey.RESPONSE.toString(), FunnyChars.ARABIC_LETTERS.getString());
        logdata.put(LogKey.CALL_TIME_UTC.toString(), FunnyChars.CYRILLIC_NUMERALS.getString());
        ILogFormatter formatter = new JsonFormatter();
        String jsonString = formatter.formatLogEntry(logdata);
        JSONObject joFromString = new JSONObject(jsonString);
        assertEquals(joFromString.getString(LogKey.SENDER.toString()), "1234");
        assertEquals(joFromString.getString(LogKey.REQUEST.toString()), FunnyChars.CYRILLIC_LETTERS.getString());
        assertEquals(joFromString.getString(LogKey.RESPONSE.toString()), FunnyChars.ARABIC_LETTERS.getString());
        assertEquals(joFromString.getString(LogKey.CALL_TIME_UTC.toString()), FunnyChars.CYRILLIC_NUMERALS.getString());
        
    }
    
}
