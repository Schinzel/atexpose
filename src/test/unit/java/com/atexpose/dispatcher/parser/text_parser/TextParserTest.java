package com.atexpose.dispatcher.parser.text_parser;

import io.schinzel.basicutils.FunnyChars;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


/**
 * @author Schinzel
 */
public class TextParserTest {

    @Test
    public void getClone_NormalTextParser_ClassTextParser() {
        String cloneClassName = new TextParser().getClone().getClass().getSimpleName();
        assertEquals(TextParser.class.getSimpleName(), cloneClassName);
    }


    @Test
    public void getMethodName_SimpleRequest_MethodName() {
        String actualMethodName = new TextParser()
                .getRequest("getMonkey")
                .getMethodName();
        assertEquals("getMonkey", actualMethodName);
    }


    @Test
    public void getMethodName_ShortMethodName_MethodName() {
        String actualMethodName = new TextParser()
                .getRequest("g")
                .getMethodName();
        assertEquals("g", actualMethodName);
    }


    @Test
    public void getMethodName_LongMethodName_MethodName() {
        String actualMethodName = new TextParser()
                .getRequest(FunnyChars.LONG_STRING.getString())
                .getMethodName();
        assertEquals(FunnyChars.LONG_STRING.getString(), actualMethodName);
    }


    @Test
    public void getArgumentNames_NoArguments_EmptyList() {
        List<String> argumentNames = new TextParser()
                .getRequest("getMonkey")
                .getArgumentNames();
        assertThat(argumentNames).isEqualTo(Collections.emptyList());
    }


    @Test
    public void getArgumentNames_TwoArguments_EmptyList() {
        List<String> argumentNames = new TextParser()
                .getRequest("getMonkey arg1, arg2")
                .getArgumentNames();
        assertThat(argumentNames).isEqualTo(Collections.emptyList());
    }


    @Test
    public void getArgumentValues_NoArguments_EmptyList() {
        List<String> argumentValues = new TextParser()
                .getRequest("getMonkey")
                .getArgumentValues();
        assertThat(argumentValues).isEmpty();
    }


    @Test
    public void getArgumentValues_OneArgument_ListOneLongAndArgName() {
        List<String> argumentValues = new TextParser()
                .getRequest("getMonkey bananas")
                .getArgumentValues();
        assertThat(argumentValues).containsExactly("bananas");
    }


    @Test
    public void getArgumentValues_TwoArguments_ListTwoLongAndArgNames() {
        List<String> argumentValues = new TextParser()
                .getRequest("getMonkey bananas, 44")
                .getArgumentValues();
        assertThat(argumentValues).containsExactly("bananas", "44");
    }


    @Test
    public void getArgumentValues_20Arguments_List20LongAndArgNames() {
        List<String> argumentValues = new TextParser()
                .getRequest("getMonkey  1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20")
                .getArgumentValues();
        assertThat(argumentValues).hasSize(20);
        assertThat(argumentValues.get(0)).isEqualTo("1");
        assertThat(argumentValues.get(6)).isEqualTo("7");
        assertThat(argumentValues.get(19)).isEqualTo("20");
    }


    @Test
    public void getArgumentValues_TwoArgumentsWithSpaces_ListTwoLongAndArgNames() {
        List<String> argumentValues = new TextParser()
                .getRequest("getMonkey          bananas      ,      44    ")
                .getArgumentValues();
        assertThat(argumentValues).containsExactly("bananas", "44");
    }


}
