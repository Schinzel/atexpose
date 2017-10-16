package com.atexpose.util.httpresponse;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class FileExtensionsTest extends FileExtensions {


    @Test
    public void getContentType_monkeyDotTxt_ContentTypeTxt() {
        ContentType actual = FileExtensions.getContentType("monkey.txt");
        ContentType expected = ContentType.TEXT_FILE;
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void getContentType_UnknownType_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> FileExtensions.getContentType("monkey.yyy"))
                .withMessageStartingWith("No know content type for file");
    }
}