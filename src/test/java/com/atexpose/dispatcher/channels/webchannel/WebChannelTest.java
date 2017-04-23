package com.atexpose.dispatcher.channels.webchannel;

import com.atexpose.dispatcher.channels.webchannel.redirect.Redirects;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class WebChannelTest {


    @Test
    public void testGetRequest_redirect_page() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .addFileRedirect("apa.html", "bear.html")
                .build();
        WebChannel webChannel = WebChannel.builder()
                .port(5555)
                .timeout(300)
                .redirects(redirects)
                .build();
        new Thread(() -> {
            //Start waiting for a request
            webChannel.getRequest(new ByteStorage());
        }).start();
        //Send request
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/apa.html")
                .followRedirects(false)
                .execute();
        //Check that got a redirect as response
        Assert.assertEquals(302, response.statusCode());
        //And that the location is correct
        Assert.assertEquals("http://127.0.0.1:5555/bear.html", response.header("location"));
        webChannel.shutdown(Thread.currentThread());
    }


    @Test
    public void testGetRequest_redirectMethodCall_shouldNotRedirect() throws Exception {
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .build();
        WebChannel webChannel = WebChannel.builder()
                .port(5555)
                .timeout(300)
                .redirects(redirects)
                .build();
        new Thread(() -> {
            Sandman.snoozeMillis(10);
            try {
                //Send request
                Connection.Response response = Jsoup
                        .connect("http://127.0.0.1:5555/call/time")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //Should redirect a page request
        ByteStorage byteStorage = new ByteStorage();
        //Start waiting for a request
        webChannel.getRequest(byteStorage);
        URI uri = new HttpRequest(byteStorage.getAsString()).getURI();
        Assert.assertEquals("http://127.0.0.1:5555/call/time", uri.toString());
        webChannel.shutdown(Thread.currentThread());
    }
}