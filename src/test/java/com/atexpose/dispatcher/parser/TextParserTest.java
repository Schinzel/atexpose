package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.EmptyObjects;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class TextParserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void test_getClone() {
        TextParser textParser = new TextParser();
        AbstractParser textClone = textParser.getClone();
        assertEquals(textParser.getClass().getSimpleName(),
                textClone.getClass().getSimpleName());
        AbstractParser textClone2 = textParser.getClone();
        assertEquals(textParser.getClass().getSimpleName(),
                textClone2.getClass().getSimpleName());
    }


    @Test
    public void test_parseRequest_methodName() {
        TextParser textParser = new TextParser();
        //
        textParser.parseRequest("getMonkey");
        assertEquals("getMonkey", textParser.getMethodName());
        //
        textParser.parseRequest("g");
        assertEquals("g", textParser.getMethodName());
        //
        String longMethodName = "0123456789_0123456789_0123456789_0123456789_0123456789_0123456789_0123456789_0123456789_0123456789_0123456789";
        textParser.parseRequest(longMethodName);
        assertEquals(longMethodName, textParser.getMethodName());

    }


    @Test
    public void test_parseRequest_arguments_names() {
        TextParser textParser = new TextParser();
        //
        textParser.parseRequest("getMonkey");
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, textParser.getArgumentNames());
        //
        textParser.parseRequest("getMonkey bananas");
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, textParser.getArgumentNames());

    }


    @Test
    public void test_parseRequest_arguments_values() {
        TextParser textParser = new TextParser();
        //
        textParser.parseRequest("getMonkey");
        assertEquals(0, textParser.getArgumentValues().length);
        //
        textParser.parseRequest("getMonkey bananas");
        assertEquals("getMonkey", textParser.getMethodName());
        assertEquals(1, textParser.getArgumentValues().length);
        assertEquals("bananas", textParser.getArgumentValues()[0]);
        //
        textParser.parseRequest("getMonkey bananas, 44");
        assertEquals("getMonkey", textParser.getMethodName());
        assertEquals(2, textParser.getArgumentValues().length);
        assertEquals("bananas", textParser.getArgumentValues()[0]);
        assertEquals("44", textParser.getArgumentValues()[1]);
        //
        textParser.parseRequest("getMonkey 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
        assertEquals("getMonkey", textParser.getMethodName());
        assertEquals(20, textParser.getArgumentValues().length);
        assertEquals("1", textParser.getArgumentValues()[0]);
        assertEquals("14", textParser.getArgumentValues()[13]);
        assertEquals("20", textParser.getArgumentValues()[19]);
        //
        textParser.parseRequest("getMonkey          bananas      ,      44    ");
        assertEquals("getMonkey", textParser.getMethodName());
        assertEquals(2, textParser.getArgumentValues().length);
        assertEquals("bananas", textParser.getArgumentValues()[0]);
        assertEquals("44", textParser.getArgumentValues()[1]);
    }


}
