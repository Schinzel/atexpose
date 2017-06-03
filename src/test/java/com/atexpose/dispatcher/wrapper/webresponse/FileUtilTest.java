package com.atexpose.dispatcher.wrapper.webresponse;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    @Test
    public void isTextFile() {
        assertTrue(FileUtil.isTextFile("m.html"));
        assertTrue(FileUtil.isTextFile("really_really_really_long_file_name.html"));
        assertTrue(FileUtil.isTextFile("dot.in.name.html"));
        assertTrue(FileUtil.isTextFile("m.HTML"));
        assertTrue(FileUtil.isTextFile("m.hTmL"));
        //
        assertTrue(FileUtil.isTextFile("file.htm"));
        assertTrue(FileUtil.isTextFile("file.css"));
        assertTrue(FileUtil.isTextFile("file.js"));
        assertTrue(FileUtil.isTextFile("file.txt"));
        //
        assertFalse(FileUtil.isTextFile("file.ico"));
        assertFalse(FileUtil.isTextFile("file.png"));
        assertFalse(FileUtil.isTextFile("file.jpg"));
        assertFalse(FileUtil.isTextFile("file.jpeg"));
        assertFalse(FileUtil.isTextFile("file.gif"));
        assertFalse(FileUtil.isTextFile("file.svg"));
        assertFalse(FileUtil.isTextFile("file.map"));

    }
}