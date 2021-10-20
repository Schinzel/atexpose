package com.atexpose.dispatcher.channels.web_channel.redirect;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class HttpsRedirectTest {


    @Test
    @SneakyThrows
    public void testShouldRedirect_And_GetNewLocation_http() {
        URI uri = new URI("http://www.example.com");
        Assert.assertTrue(new HttpsRedirect().shouldRedirect(uri));
        Assert.assertEquals("https://www.example.com", new HttpsRedirect().getNewLocation(uri).toString());
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_And_GetNewLocation_HTTP_uppercase() {
        URI uri = new URI("HTTP://www.example.com");
        Assert.assertTrue(new HttpsRedirect().shouldRedirect(uri));
        Assert.assertEquals("https://www.example.com", new HttpsRedirect().getNewLocation(uri).toString());
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_https() {
        URI uri = new URI("https://www.example.com");
        Assert.assertFalse(new HttpsRedirect().shouldRedirect(uri));
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_HTTPS_uppercase() {
        URI uri = new URI("HTTPS://www.example.com");
        Assert.assertFalse(new HttpsRedirect().shouldRedirect(uri));
    }


    @Test
    @SneakyThrows
    public void testGetRedirect_argumentShouldNotChange() {
        URI uri = new URI("http://www.example.com");
        Assert.assertEquals("https://www.example.com", new HttpsRedirect().getNewLocation(uri).toString());
        Assert.assertEquals("http://www.example.com", uri.toString());
    }

}
