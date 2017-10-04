package com.atexpose.dispatcher.channels.webchannel;

import com.atexpose.dispatcher.channels.webchannel.redirect.Redirects;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.Sandman;
import io.schinzel.basicutils.UTF8;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.*;


public class WebChannelTest {


    @Test
    public void getRequest_PageRequest_ShouldRedirect() throws Exception {
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
        //Snooze for tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void getRequest_MethodCall_SrouldNotRedirect() throws Exception {
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
                        .connect("http://127.0.0.1:5555/call/ping")
                        .followRedirects(false)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //Should redirect a page request
        ByteStorage byteStorage = new ByteStorage();
        //Start waiting for a request
        webChannel.getRequest(byteStorage);
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Date: Sat, 29 Apr 2017 05:58:31 GMT\r\n" +
                "Connection: keep-alive\r\n" +
                "Server: @Expose\r\n" +
                "Content-Length: 4\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Via: 1.1 vegur\r\n" +
                "\r\n"
                + "pong";
        webChannel.writeResponse(UTF8.getBytes(httpResponse));
        URI uri = new HttpRequest(byteStorage.getAsString()).getURI();
        Assert.assertEquals("http://127.0.0.1:5555/call/ping", uri.toString());
        webChannel.shutdown(Thread.currentThread());
        //Snooze for tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    /**
     * 2017-10-04 Test of real life issue. Below request is a request from browser sync. That
     * caused an null pointer exception.
     */
    @Test
    public void getDirectResponse_BrowserSyncRequest_EmptyString() {
        String browserSyncRequest = "" +
                "GET / HTTP/1.1\r\n" +
                "upgrade-insecure-requests: 1\r\n" +
                "connection: close\r\n" +
                "cookie: _ga=GA1.1.1128886252.1505742355; __distillery=v20150227_357dec24-18f0-4a4f-bda6-7970b7774950; _gid=GA1.1.1317470888.1506937146\r\n" +
                "accept-encoding: identity\r\n" +
                "accept-language: sv-SE,sv;q=0.8,en-US;q=0.5,en;q=0.3\r\n" +
                "accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:56.0) Gecko/20100101 Firefox/56.0\r\n" +
                "host: 192.168.136.105:5555\r\n" +
                "agent: false\r\n" +
                "\r\n";
        HttpRequest httpRequest = new HttpRequest(browserSyncRequest);
        String path = httpRequest.getURI().getHost();
        System.out.println("path: " + path);
        Redirects redirects = Redirects.getBuilder()
                .setHttpsRedirect()
                .addHostRedirect("schinzel.io", "www.schinzel.io")
                .build();
        WebChannel webChannel = WebChannel.builder()
                .port(5555)
                .timeout(300)
                .redirects(redirects)
                .build();
        String redirectResponse = webChannel.getDirectResponse(httpRequest);
        assertThat(redirectResponse).isEmpty();
    }

}