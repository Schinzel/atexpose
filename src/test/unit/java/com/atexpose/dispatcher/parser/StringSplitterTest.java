/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atexpose.dispatcher.parser;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author schinzel
 */
public class StringSplitterTest extends StringSplitter {

    @Test
    public void testSplitNoQualifiers() {
        String str;
        List<String> actual;
        //
        str = "1,2,3,4,5";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(5);
        assertThat(actual).containsSequence("1", "2", "3", "4", "5");
        //
        str = "1";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(1);
        assertThat(actual).containsSequence("1");
        //
        str = "1,2";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(2);
        assertThat(actual).containsSequence("1", "2");
        //
        str = "";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).isEmpty();
        //
        str = null;
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).isEmpty();
        //
        str = " ,  ,   ";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).isEmpty();
        //
        str = "0123456789_0123456789_0123456789_0123456789_0123456789";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(1);
        assertThat(actual).containsSequence("0123456789_0123456789_0123456789_0123456789_0123456789");
        //
        str = "0123456789_0123456789_0123456789_0123456789_0123456789,0123456789_0123456789_0123456789_0123456789_0123456789";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(2);
        assertThat(actual).containsSequence("0123456789_0123456789_0123456789_0123456789_0123456789", "0123456789_0123456789_0123456789_0123456789_0123456789");
    }


    @Test
    public void testSplitOnComma_DoubleQuoteQualifer() {
        String str;
        List<String> actual;
        //
        str = "1,\"2,3,4,5\"";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(2);
        assertThat(actual).containsSequence("1", "2,3,4,5");
        //
        str = "\"1,2,3,4,5\"";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(1);
        assertThat(actual).containsSequence("1,2,3,4,5");
        //
        str = "\"1,,,2,,,3,,,4,,,5\"";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(1);
        assertThat(actual).containsSequence("1,,,2,,,3,,,4,,,5");
        //
        str = "\"2,3,4,5\",6,7,8";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(4);
        assertThat(actual).containsSequence("2,3,4,5", "6", "7", "8");
        //
        str = "\"2,3,4,5\",6,7,8,\"9,10\"";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(5);
        assertThat(actual).containsSequence("2,3,4,5", "6", "7", "8", "9,10");
        //
        str = "1,\'2,\"3,4\",5\'";
        actual = StringSplitter.splitOnComma_DoubleQuoteQualifier(str);
        assertThat(actual).hasSize(4);
        assertThat(actual).containsSequence("1", "'2", "3,4", "5'");
    }

}
