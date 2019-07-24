package com.atexpose.dispatcher.channels.webchannel.redirect;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class HostRedirectTest {

    @Test
    public void shouldRedirect_should_not_redirect() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("www.example.com", "www.example.com");
        URI uri = new URI("http://example.com");
        Assert.assertFalse(hostRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_root_to_www() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("example.com", "www.example.com");
        URI uri = new URI("http://example.com");
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.example.com", hostRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_another_domain() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("www.example.com", "www.schinzel.io");
        URI uri = new URI("http://www.example.com");
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.schinzel.io", hostRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_subdomain() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("sub1.example.com", "sub2.example.com");
        URI uri = new URI("http://sub1.example.com");
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://sub2.example.com", hostRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void shouldRedirect_And_GetNewLocation_https() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("sub1.example.com", "sub2.example.io");
        URI uri = new URI("https://sub1.example.com");
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
        Assert.assertEquals("https://sub2.example.io", hostRedirect.getNewLocation(uri).toString());
    }


    /**
     * Testing with query string with polish, persian and cyrillic chars.
     */
    @Test
    public void shouldRedirect_And_GetNewLocation_with_querystring() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("www.example.com", "www.schinzel.io");
        URI uri = new URI("http://www.example.com?k1=ŹźŻż&k2=سش&k3=ДЂ");
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
        Assert.assertEquals("http://www.schinzel.io?k1=ŹźŻż&k2=سش&k3=ДЂ", hostRedirect.getNewLocation(uri).toString());
    }

    @Test
    public void getNewLocation_HtmlPage_HtmlPageFromRedirectedToDomain() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("www.example.com", "www.schinzel.io");
        URI uri = new URI("http://www.example.com/mypage.html");
        Assert.assertEquals("http://www.schinzel.io/mypage.html", hostRedirect.getNewLocation(uri).toString());
    }


    @Test
    public void getNewLocation_HtmlPageWithQueryParam_HtmlPageFromRedirectedToDomain() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("www.example.com", "www.schinzel.io");
        URI uri = new URI("http://www.example.com/mypage.html?param=123");
        Assert.assertEquals("http://www.schinzel.io/mypage.html?param=123", hostRedirect.getNewLocation(uri).toString());
    }



    @Test
    public void shouldRedirect_And_GetNewLocation_checkThatArgumentUriUnchanged() throws Exception {
        HostRedirect hostRedirect = new HostRedirect("example.com", "www.example.com");
        URI uri = new URI("http://example.com");
        Assert.assertEquals("http://www.example.com", hostRedirect.getNewLocation(uri).toString());
        Assert.assertEquals("http://example.com", uri.toString());
    }


}