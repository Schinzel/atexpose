package com.atexpose.dispatcher.channels.webchannel.redirect;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-04-21.
 */
public class HttpsRedirectTest {


    @Test
    @SneakyThrows
    public void testShouldRedirect_http() {
        URI uri = new URIBuilder("http://www.example.com").build();
        Assert.assertTrue(HttpsRedirect.create().shouldRedirect(uri));
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_HTTP_uppercase() {
        URI uri = new URIBuilder("HTTP://www.example.com").build();
        Assert.assertTrue(HttpsRedirect.create().shouldRedirect(uri));
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_https() {
        URI uri = new URIBuilder("https://www.example.com").build();
        Assert.assertFalse(HttpsRedirect.create().shouldRedirect(uri));
    }


    @Test
    @SneakyThrows
    public void testShouldRedirect_HTTPS_uppercase() {
        URI uri = new URIBuilder("HTTPS://www.example.com").build();
        Assert.assertFalse(HttpsRedirect.create().shouldRedirect(uri));
    }
}
