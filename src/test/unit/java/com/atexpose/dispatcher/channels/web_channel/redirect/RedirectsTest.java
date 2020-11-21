package com.atexpose.dispatcher.channels.web_channel.redirect;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class RedirectsTest {
    @Test
    public void shouldRedirectAndGetNewLocation() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .build();
        URI uri = new URI("http://example.com/a/b/c/source.html?k1=ŹźŻż&k2=سش&k3=ДЂ");
        Assert.assertTrue(redirects.shouldRedirect(uri));
        Assert.assertEquals("https://www.schinzel.io/q/r/s/dest.html?k1=ŹźŻż&k2=سش&k3=ДЂ", redirects.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_noRedirect() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .build();
        URI uri = new URI("https://www.google.com");
        Assert.assertFalse(redirects.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirectAndGetNewLocation_failWhale() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .setFailWhaleRedirect("bapp.html")
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .build();
        URI uri = new URI("https://www.schinzel.io");
        //The only redirect clause that hits is the fail whale.
        Assert.assertTrue(redirects.shouldRedirect(uri));
        Assert.assertEquals("https://www.schinzel.io/bapp.html", redirects.getNewLocation(uri).toString());
    }


    @Test
    public void getNewLocation_failWhale_multipleRedirectsHits() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setFailWhaleRedirect("bapp.html")
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .setHttpsRedirect()
                .build();
        URI uri = new URI("http://example.com/a/b/c/source.html");
        Assert.assertEquals("https://www.schinzel.io/bapp.html", redirects.getNewLocation(uri).toString());
    }


    @Test
    public void getNewLocation_MethodCall() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setFailWhaleRedirect("bapp.html")
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .setHttpsRedirect()
                .build();
        URI uri = new URI("http://example.com/api/time");
        Assert.assertEquals("https://www.schinzel.io/bapp.html", redirects.getNewLocation(uri).toString());
    }


    @Test
    public void testIsMethodCall_htmlPageRequest() throws Exception {
        URI uri = new URI("https://www.example.com/subdir/bear.html");
        Assert.assertFalse(Redirects.isMethodCall(uri));
    }

    @Test
    public void testIsMethodCall_methodCall() throws Exception {
        URI uri = new URI("https://www.example.com/api/time");
        Assert.assertTrue(Redirects.isMethodCall(uri));
    }

}
