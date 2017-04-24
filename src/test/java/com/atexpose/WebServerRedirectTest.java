package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-04-24.
 */
public class WebServerRedirectTest {


    @Test
    public void testWebServerRedirects() throws IOException {
        int statusCode;
        String location;
        Connection.Response response;
        Dispatcher webServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
        //Basic test
        response = Jsoup
                .connect("http://127.0.0.1:5555/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("http://127.0.0.1:5555/dest.html", location);
        //Test with dirs
        response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html", location);
        //Test that query strings are passed on
        response = Jsoup
                .connect("http://127.0.0.1:5555/src.html?key1=val1")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("http://127.0.0.1:5555/dest.html?key1=val1", location);
        //Test that query strings are passed on with dirs
        response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html?key2=val2", location);
        webServer.shutdown();
    }
}
