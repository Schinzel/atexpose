package com.atexpose.dispatcher.channels.webchannel.redirect;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class FileRedirectTest {
    @Test
    public void shouldRedirect_should_not_redirect() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("summary.html")
                .to("index.html")
                .build();
        URI uri = new URIBuilder("http://www.example.com/another.html").build();
        Assert.assertFalse(fileRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldAndGetRedirect_basic_noSlashFirst() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("summary.html")
                .to("index.html")
                .build();
        URI uri = new URIBuilder("http://www.example.com/summary.html").build();
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getRedirect(uri).toString());
    }


    @Test
    public void shouldAndGetRedirect_basic() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("/summary.html")
                .to("/index.html")
                .build();
        URI uri = new URIBuilder("http://www.example.com/summary.html").build();
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getRedirect(uri).toString());
    }


    @Test
    public void shouldAndGetRedirect_sub_dir() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("/a/b/c/summary.html")
                .to("q/r/s/index.html")
                .build();
        URI uri = new URIBuilder("http://www.example.com/a/b/c/summary.html").build();
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com/q/r/s/index.html", fileRedirect.getRedirect(uri).toString());
    }


    @Test
    public void shouldAndGetRedirect_subdir_https_querystring() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("/a/b/c/summary.html")
                .to("q/r/s/index.html")
                .build();
        URI uri = new URIBuilder("https://www.example.com/a/b/c/summary.html?k1=ŹźŻż&k2=سش&k3=ДЂ").build();
        Assert.assertTrue(fileRedirect.shouldRedirect(uri));
        Assert.assertEquals("https://www.example.com/q/r/s/index.html?k1=ŹźŻż&k2=سش&k3=ДЂ", fileRedirect.getRedirect(uri).toString());
    }


    @Test
    public void shouldAndGetRedirect_checkThatArgumentUriUnchanged() throws Exception {
        FileRedirect fileRedirect = FileRedirect.builder()
                .from("/summary.html")
                .to("/index.html")
                .build();
        URI uri = new URIBuilder("http://www.example.com/summary.html").build();
        Assert.assertEquals("http://www.example.com/index.html", fileRedirect.getRedirect(uri).toString());
        Assert.assertEquals("http://www.example.com/summary.html", uri.toString());
    }

}