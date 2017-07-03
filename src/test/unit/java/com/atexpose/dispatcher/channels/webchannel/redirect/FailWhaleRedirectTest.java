package com.atexpose.dispatcher.channels.webchannel.redirect;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class FailWhaleRedirectTest {
    @Test
    public void shouldRedirect_isOnOtherPage() throws Exception {
        FailWhaleRedirect failWhaleRedirect = new FailWhaleRedirect("info.html");
        URI uri = new URI("http://www.example.com/another.html");
        Assert.assertTrue(failWhaleRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirect_isOnFailWhalePage() throws Exception {
        FailWhaleRedirect failWhaleRedirect = new FailWhaleRedirect("info.html");
        URI uri = new URI("http://www.example.com/info.html");
        Assert.assertFalse(failWhaleRedirect.shouldRedirect(uri));
    }


    @Test
    public void getNewLocation() throws Exception {
        FailWhaleRedirect failWhaleRedirect = new FailWhaleRedirect("info.html");
        URI uri = new URI("http://www.example.com/another.html");
        Assert.assertEquals("http://www.example.com/info.html", failWhaleRedirect.getNewLocation(uri).toString());
    }

}