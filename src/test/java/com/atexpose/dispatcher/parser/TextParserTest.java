package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.FunnyChars;
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
    public void getClone_NormalTextParser_ClassTextPraser() {
        String cloneClassName = new TextParser2().getClone().getClass().getSimpleName();
        assertEquals(TextParser2.class.getSimpleName(), cloneClassName);
    }


    @Test
    public void getMethodName_SimpleRequest_MethodName() {
        String actualMethodName = new TextParser2()
                .getRequest("getMonkey")
                .getMethodName();
        assertEquals("getMonkey", actualMethodName);
    }


    @Test
    public void getMethodName_ShortMethodName_MethodName() {
        String actualMethodName = new TextParser2()
                .getRequest("g")
                .getMethodName();
        assertEquals("g", actualMethodName);
    }


    @Test
    public void getMethodName_LongMethodName_MethodName() {
        String actualMethodName = new TextParser2()
                .getRequest(FunnyChars.LONG_STRING.getString())
                .getMethodName();
        assertEquals(FunnyChars.LONG_STRING.getString(), actualMethodName);
    }


    @Test
    public void getArgumentNames_NoArguments_EmptyArray() {
        String[] argumentNames = new TextParser2()
                .getRequest("getMonkey")
                .getArgumentNames();
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, argumentNames);
    }


    @Test
    public void getArgumentNames_TwoArguments_EmptyArray() {
        String[] argumentNames = new TextParser2()
                .getRequest("getMonkey arg1, arg2")
                .getArgumentNames();
        assertArrayEquals(EmptyObjects.EMPTY_STRING_ARRAY, argumentNames);
    }


    @Test
    public void getArgumentValues_NoArguments_EmptyArray() {
        String[] argumentValues = new TextParser2()
                .getRequest("getMonkey")
                .getArgumentValues();
        assertEquals(0, argumentValues.length);
    }


    @Test
    public void getArgumentValues_OneArgument_ArrayOneLongAndArgName() {
        String[] argumentValues = new TextParser2()
                .getRequest("getMonkey bananas")
                .getArgumentValues();
        assertEquals(1, argumentValues.length);
        assertEquals("bananas", argumentValues[0]);
    }


    @Test
    public void getArgumentValues_TwoArguments_ArrayTwoLongAndArgNames() {
        String[] argumentValues = new TextParser2()
                .getRequest("getMonkey bananas, 44")
                .getArgumentValues();
        assertEquals(2, argumentValues.length);
        assertEquals("bananas", argumentValues[0]);
        assertEquals("44", argumentValues[1]);
    }


    @Test
    public void getArgumentValues_20Arguments_Array20LongAndArgNames() {
        String[] argumentValues = new TextParser2()
                .getRequest("getMonkey  1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20")
                .getArgumentValues();
        assertEquals(20, argumentValues.length);
        assertEquals("1", argumentValues[0]);
        assertEquals("7", argumentValues[6]);
        assertEquals("20", argumentValues[19]);
    }


    @Test
    public void getArgumentValues_TwoArgumentsWithSpaces_ArrayTwoLongAndArgNames() {
        String[] argumentValues = new TextParser2()
                .getRequest("getMonkey          bananas      ,      44    ")
                .getArgumentValues();
        assertEquals(2, argumentValues.length);
        assertEquals("bananas", argumentValues[0]);
        assertEquals("44", argumentValues[1]);
    }


}
