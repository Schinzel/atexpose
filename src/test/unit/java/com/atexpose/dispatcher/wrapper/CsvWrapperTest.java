package com.atexpose.dispatcher.wrapper;

import io.schinzel.basicutils.FunnyChars;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;


/**
 * @author schinzel
 */
public class CsvWrapperTest {


    @Test
    public void testWrapResponseString() {
        CsvWrapper csvWrapper = new CsvWrapper();
        String result;
        for (FunnyChars funnyString : FunnyChars.values()) {
            result = csvWrapper.wrapResponse(funnyString.getString());
            assertEquals(funnyString.getString(), result);
        }
    }


    @Test
    public void testWrapError() {
        CsvWrapper csvWrapper = new CsvWrapper();
        String result;
        for (FunnyChars funnyString : FunnyChars.values()) {
            Map<String, String> map = Collections.singletonMap("message", funnyString.getString());
            result = csvWrapper.wrapError(map);
            assertEquals("Error:\nmessage: " + funnyString.getString(), result);
        }
    }


    @Test
    public void testGetStatusAsJson() {
        CsvWrapper csvWrapper = new CsvWrapper();
        JSONObject joStatus = csvWrapper.getState().getJson();
        String statusAsString = joStatus.toString();
        assertEquals("{\"ColumnDelimiter\":\", \",\"Class\":\"CsvWrapper\"}", statusAsString);

    }


    @Test
    public void testWrapFile() throws JSONException {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> new CsvWrapper().wrapFile("anyfile.txt"))
                .withMessageStartingWith("Not supported yet.");
    }
}
