package com.atexpose.dispatcher.channels.web_channel.redirect;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class FileRedirectTest {
    @Test
    public void shouldRedirect_shouldNotRedirect() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("summary.html", "index.html");
        URI uri = new URI("http://www.example.com/another.html");
        Assert.assertFalse(fileRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_basic_noSlashFirst() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("summary.html", "index.html");
        URI uri = new URI("http://www.example.com/summary.html");
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_basic() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("/summary.html", "/index.html");
        URI uri = new URI("http://www.example.com/summary.html");
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_sub_dir() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("/a/b/c/summary.html", "q/r/s/index.html");
        URI uri = new URI("http://www.example.com/a/b/c/summary.html");
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/q/r/s/index.html", fileRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_subdir_https_querystring() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("/a/b/c/summary.html", "q/r/s/index.html");
        URI uri = new URI("https://www.example.com/a/b/c/summary.html?k1=ŹźŻż&k2=سش&k3=ДЂ");
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("https://www.example.com/q/r/s/index.html?k1=ŹźŻż&k2=سش&k3=ДЂ", fileRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_checkThatArgumentUriUnchanged() throws Exception {
        FileRedirect fileRedirect = new FileRedirect("/summary.html", "/index.html");
        URI uri = new URI("http://www.example.com/summary.html");
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getNewLocation(uri).toString());
        Assert.assertEquals("http://www.example.com/summary.html", uri.toString());
    }

}