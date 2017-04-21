package com.atexpose.dispatcher.channels.webchannel.redirect;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class RedirectsTest {
    @Test
    public void shouldAndGetRedirect() throws Exception {
        ImmutableList<IRedirect> myList = new ImmutableList.Builder<IRedirect>()
                .add(HttpsRedirect.create())
                .add(HostRedirect.create("example.com", "www.example.com"))
                .add(HostRedirect.create("www.example.com", "www.schinzel.io"))
                .add(FileRedirect.create("a/b/c/source.html", "q/r/s/dest.html"))
                .build();
        Redirects redirects = new Redirects(myList);
        URI uri = new URI("http://example.com/a/b/c/source.html?k1=ŹźŻż&k2=سش&k3=ДЂ");
        Assert.assertTrue(redirects.shouldRedirect(uri));
        Assert.assertEquals("https://www.schinzel.io/q/r/s/dest.html?k1=ŹźŻż&k2=سش&k3=ДЂ", redirects.getRedirects(uri).toString());
    }


}