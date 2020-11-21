package com.atexpose.dispatcher.parser.url_parser;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author schinzel
 */
public class UrlCodingTest {
    Map<String, String> map = new HashMap<>();


    @Before
    public void before() {
        //String to decode have been genereted using encodeURIComponent in Chrome 40.0.2214.94 (64-bit) on Yosimite 10.10.2 (14C109)
        map.put("1234567890 abcdefghijklmonpqrstuvwxyz", "1234567890%20abcdefghijklmonpqrstuvwxyz");
        map.put("åäö", "%C3%A5%C3%A4%C3%B6");
        map.put("! \" # € % & / ( ) =", "!%20%22%20%23%20%E2%82%AC%20%25%20%26%20%2F%20(%20)%20%3D");
        //Polish
        map.put("ŹźŻż", "%C5%B9%C5%BA%C5%BB%C5%BC");
        //Danish
        map.put("ÆæØøÅå", "%C3%86%C3%A6%C3%98%C3%B8%C3%85%C3%A5");
        //Cyrillic numerals
        map.put("АВГДЄЅЗИѲ", "%D0%90%D0%92%D0%93%D0%94%D0%84%D0%85%D0%97%D0%98%D1%B2");
        //Cyrillic letters
        map.put("ДЂЙЩ", "%D0%94%D0%82%D0%99%D0%A9");
        map.put(",;.:-_'*^¨~", "%2C%3B.%3A-_'*%5E%C2%A8~");
        map.put("/*-+", "%2F*-%2B");
    }


    @Test
    public void testEncodeURIComponent() {
        int counter = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = UrlCoding.encodeURIComponent(entry.getKey());
            assertEquals(entry.getValue(), value);
            counter++;
        }
        // make sure we have tested all
        assertEquals(9, counter);

    }


    /**
     * Test of decodeURIComponent method, of class UrlCoding.
     */
    @Test
    public void testDecodeURIComponent() {
        int counter = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = UrlCoding.decodeURIComponent(entry.getValue());
            assertEquals(entry.getKey(), value);
            counter++;
        }
        // make sure we have tested all
        assertEquals(9, counter);

    }

}
