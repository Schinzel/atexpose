package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebServerRedirectTest {
    Dispatcher mWebServer;


    @Before
    public void before() {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .forceHttps(true)
                .addHostRedirect("127.0.0.1", "localhost")
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
    }


    @After
    public void after() {
        mWebServer.shutdown();
        //Snooze required to get tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void WebServerRedirect_PageInRoot_ShouldRedirect() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dest.html", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInSubDir_ShouldRedirect() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dirdest/dest.html", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInRootWithQueryString_ShouldRedirectWithQuery() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html?key1=val1")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dest.html?key1=val1", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInSubDirWithQueryString_ShouldRedirectWithQuery() throws Exception {
        //Test that query strings are passed on with dirs
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dirdest/dest.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_MethodCall_ShouldNotRedirect() throws Exception {
        //Test that query strings are passed on with dirs
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/call/ping")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(200, response.statusCode());
        assertEquals("pong", response.body());
    }
}
