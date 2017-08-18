package com.atexpose.dispatcher.logging.format;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.FunnyChars;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class JsonFormatterTest {


    @Test
    public void testFormatLogEntry() {
        Map<String, String> logdata = ImmutableMap.<String, String>builder()
                .put("key_1", "1234")
                .put("key_2", FunnyChars.CYRILLIC_LETTERS.getString())
                .put("key_3", FunnyChars.ARABIC_LETTERS.getString())
                .put("key_4", FunnyChars.CYRILLIC_NUMERALS.getString())
                .build();
        String jsonString = new JsonFormatter().formatLogEntry(logdata);
        JSONObject joFromString = new JSONObject(jsonString);
        assertEquals(joFromString.getString("key_1"), "1234");
        assertEquals(joFromString.getString("key_2"), FunnyChars.CYRILLIC_LETTERS.getString());
        assertEquals(joFromString.getString("key_3"), FunnyChars.ARABIC_LETTERS.getString());
        assertEquals(joFromString.getString("key_4"), FunnyChars.CYRILLIC_NUMERALS.getString());
    }

}
