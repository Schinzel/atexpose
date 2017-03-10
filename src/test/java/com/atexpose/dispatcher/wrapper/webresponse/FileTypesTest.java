package com.atexpose.dispatcher.wrapper.webresponse;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * @author Schinzel
 */
public class FileTypesTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testIsTextFile() {
        FileTypes fileTypes = FileTypes.getInstance();
        assertTrue(fileTypes.getProps("txt").isTextFile());
        assertTrue(fileTypes.getProps("html").isTextFile());
        assertTrue(fileTypes.getProps("htm").isTextFile());
        assertFalse(fileTypes.getProps("jpg").isTextFile());
        assertFalse(fileTypes.getProps("pdf").isTextFile());
    }


    @Test
    public void test_getHeaderContentType() {
        FileTypes fileTypes = FileTypes.getInstance();
        assertEquals("text/html", fileTypes.getProps("html").getHeaderContentType());
        assertEquals("text/html", fileTypes.getProps("htm").getHeaderContentType());
        assertEquals("application/pdf", fileTypes.getProps("pdf").getHeaderContentType());
        assertEquals("image/png", fileTypes.getProps("png").getHeaderContentType());
    }

}
