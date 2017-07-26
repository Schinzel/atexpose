package com.atexpose.dispatcher.logging.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author schinzel
 */
public class SingleLineFormatTest extends SingleLineFormatter {

    @Test
    public void testGetEntryKeyToValueDelimiter() {
        SingleLineFormatter slf = new SingleLineFormatter();
        assertEquals(null, slf.getEntryKeyToValueDelimiter());
    }


    @Test
    public void testGetEntryValueDelimiter() {
        SingleLineFormatter slf = new SingleLineFormatter();
        assertEquals(",", slf.getEntryValueDelimiter());
    }


    @Test
    public void testGetEntryDelimiter() {
        SingleLineFormatter slf = new SingleLineFormatter();
        assertEquals("\n", slf.getEntryDelimiter());
    }


    @Test
    public void testGetEntryValueQualifier() {
        SingleLineFormatter slf = new SingleLineFormatter();
        assertEquals("", slf.getEntryValueQualifier());
    }


    @Test
    public void testEscapeValue() {
        SingleLineFormatter slf = new SingleLineFormatter();
        assertEquals(null, slf.escapeValue(null));
        //

    }

}
