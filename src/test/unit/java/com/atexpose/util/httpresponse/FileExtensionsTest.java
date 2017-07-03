package com.atexpose.util.httpresponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class FileExtensionsTest extends FileExtensions {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void getContentType_monkeyDotTxt_ContentTypeTxt() {
        ContentType actual = FileExtensions.getContentType("monkey.txt");
        ContentType expected = ContentType.TEXT_FILE;
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void getContentType_UnknownType_Exception() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("No know content type for file");
        FileExtensions.getContentType("monkey.yyy");
    }
}