/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atexpose.dispatcher.parser;

import com.atexpose.dispatcher.parser.StringSplitter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author schinzel
 */
public class StringSplitterTest extends StringSplitter {

    @Test
    public void testSplitNoQualifiers() {
        String str;
        String[] result1, result2;
        //
        str = "1,2,3,4,5";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(5, result1.length);
        assertEquals(5, result2.length);
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, result1);
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, result2);
        //
        str = "1";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(1, result1.length);
        assertEquals(1, result2.length);
        assertArrayEquals(new String[]{"1"}, result1);
        assertArrayEquals(new String[]{"1"}, result2);
        //
        str = "1,2";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(2, result1.length);
        assertEquals(2, result2.length);
        assertArrayEquals(new String[]{"1", "2"}, result1);
        assertArrayEquals(new String[]{"1", "2"}, result2);
        //
        str = "";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(0, result1.length);
        assertEquals(0, result2.length);
        assertArrayEquals(new String[]{}, result1);
        assertArrayEquals(new String[]{}, result2);
        //
        str = null;
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(0, result1.length);
        assertEquals(0, result2.length);
        assertArrayEquals(new String[]{}, result1);
        assertArrayEquals(new String[]{}, result2);
        //
        str = " ,  ,   ";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(0, result1.length);
        assertEquals(0, result2.length);
        assertArrayEquals(new String[]{}, result1);
        assertArrayEquals(new String[]{}, result2);
        //
        str = "0123456789_0123456789_0123456789_0123456789_0123456789";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(1, result1.length);
        assertEquals(1, result2.length);
        assertArrayEquals(new String[]{"0123456789_0123456789_0123456789_0123456789_0123456789"}, result1);
        assertArrayEquals(new String[]{"0123456789_0123456789_0123456789_0123456789_0123456789"}, result2);
        //
        str = "0123456789_0123456789_0123456789_0123456789_0123456789,0123456789_0123456789_0123456789_0123456789_0123456789";
        result1 = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        result2 = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(2, result1.length);
        assertEquals(2, result2.length);
        assertArrayEquals(new String[]{"0123456789_0123456789_0123456789_0123456789_0123456789", "0123456789_0123456789_0123456789_0123456789_0123456789"}, result1);
        assertArrayEquals(new String[]{"0123456789_0123456789_0123456789_0123456789_0123456789", "0123456789_0123456789_0123456789_0123456789_0123456789"}, result2);
    }


    @Test
    public void testSplitOnComma_DoubleQuoteQualifer() {
        String str;
        String[] result;
        //
        str = "1,\"2,3,4,5\"";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(2, result.length);
        assertArrayEquals(new String[]{"1", "2,3,4,5"}, result);
        //
        str = "\"1,2,3,4,5\"";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"1,2,3,4,5"}, result);
        //
        str = "\"1,,,2,,,3,,,4,,,5\"";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"1,,,2,,,3,,,4,,,5"}, result);
        //
        str = "\"2,3,4,5\",6,7,8";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(4, result.length);
        assertArrayEquals(new String[]{"2,3,4,5", "6", "7", "8"}, result);
        //
        str = "\"2,3,4,5\",6,7,8,\"9,10\"";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(5, result.length);
        assertArrayEquals(new String[]{"2,3,4,5", "6", "7", "8", "9,10"}, result);
        //
        str = "1,\'2,\"3,4\",5\'";
        result = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertEquals(4, result.length);
        assertArrayEquals(new String[]{"1", "'2","3,4","5'"}, result);
    }

    
    
    @Test
    public void testSplitOnComma_SingleQuoteQualifer() {
        String str;
        String[] result;
        //
        str = "1,\'2,3,4,5\'";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(2, result.length);
        assertArrayEquals(new String[]{"1", "2,3,4,5"}, result);
        //
        str = "\'1,2,3,4,5\'";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"1,2,3,4,5"}, result);
        //
        str = "\'1,,,2,,,3,,,4,,,5\'";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(1, result.length);
        assertArrayEquals(new String[]{"1,,,2,,,3,,,4,,,5"}, result);
        //
        str = "\'2,3,4,5\',6,7,8";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(4, result.length);
        assertArrayEquals(new String[]{"2,3,4,5", "6", "7", "8"}, result);
        //
        str = "\'2,3,4,5\',6,7,8,\'9,10\'";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(5, result.length);
        assertArrayEquals(new String[]{"2,3,4,5", "6", "7", "8", "9,10"}, result);
        //
        str = "1,\"2,\'3,4\',5\"";
        result = StringSplitter.splitOnComma_SingleQuoteQualifier(str);
        assertEquals(4, result.length);
        assertArrayEquals(new String[]{"1", "\"2","3,4","5\""}, result);
    }
    
}
