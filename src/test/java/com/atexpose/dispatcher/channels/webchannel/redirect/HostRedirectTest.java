package com.atexpose.dispatcher.channels.webchannel.redirect;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class HostRedirectTest {
    @Test
    public void shouldRedirect_root_to_www() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("example.com")
                .to("www.example.com")
                .build();
        URI uri = new URIBuilder("http://example.com").build();
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirect_another_domain() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("www.example.com")
                .to("www.schinzel.io")
                .build();
        URI uri = new URIBuilder("http://www.example.com").build();
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
    }


    @Test
    public void shouldRedirect_subdomain() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("sub1.example.com")
                .to("sub2.example.com")
                .build();
        URI uri = new URIBuilder("http://sub1.example.com").build();
        Assert.assertTrue(hostRedirect.shouldRedirect(uri));
    }


    @Test
    public void getRedirect_root_to_www() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("example.com")
                .to("www.example.com")
                .build();
        URI uri = new URIBuilder("http://example.com").build();
        Assert.assertEquals("http://www.example.com/", hostRedirect.getRedirect(uri).toString());
    }


    @Test
    public void getRedirect_another_domain() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("www.example.com")
                .to("www.schinzel.io")
                .build();
        URI uri = new URIBuilder("http://www.example.com").build();
        Assert.assertEquals("http://www.schinzel.io/", hostRedirect.getRedirect(uri).toString());
    }


    @Test
    public void getdRedirect_subdomain() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("sub1.example.com")
                .to("sub2.example.io")
                .build();
        URI uri = new URIBuilder("http://sub1.example.com").build();
        Assert.assertEquals("http://sub2.example.io/", hostRedirect.getRedirect(uri).toString());
    }


    /**
     * Testing with query string with polish, persian and cyrillic chars.
     */
    @Test
    public void getdRedirect_with_querystring() throws Exception {
        HostRedirect hostRedirect = HostRedirect.builder()
                .from("www.example.com")
                .to("www.schinzel.io")
                .build();
        URI uri = new URIBuilder("http://www.example.com?k1=ŹźŻż&k2=سش&k3=ДЂ").build();
        Assert.assertEquals("http://www.schinzel.io/?k1=ŹźŻż&k2=سش&k3=ДЂ", hostRedirect.getRedirect(uri).toString());
    }

}