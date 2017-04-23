package com.atexpose.dispatcher.channels.webchannel.redirect;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class RedirectsTest {
    @Test
    public void shouldAndGetRedirect() throws Exception {
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
    public void shouldRedirect_noRedidect() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .addHostRedirect("example.com", "www.example.com")
                .addHostRedirect("www.example.com", "www.schinzel.io")
                .addFileRedirect("a/b/c/source.html", "q/r/s/dest.html")
                .build();
        URI uri = new URI("https://www.google.com");
        Assert.assertFalse(redirects.shouldRedirect(uri));
    }

}