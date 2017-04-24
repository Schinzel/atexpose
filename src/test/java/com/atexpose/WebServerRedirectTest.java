package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebServerRedirectTest {
    Dispatcher mWebServer;


    @After
    public void after() {
        mWebServer.shutdown();
        //Snooze required to get tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void WebServerRedirect_PageInRoot_ShouldRedirect() throws Exception {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .startWebServer();
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dest.html", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInSubDir_ShouldRedirect() throws Exception {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInRootWithQueryString_ShouldRedirectWithQuery() throws Exception {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .startWebServer();
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html?key1=val1")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dest.html?key1=val1", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_PageInSubDirWithQueryString_ShouldRedirectWithQuery() throws Exception {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_MethodCall_ShouldNotRedirect() throws Exception {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .forceHttps(true)
                .addHostRedirect("127.0.0.1", "localhost")
                .startWebServer();
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/call/ping")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(200, response.statusCode());
        assertEquals("pong", response.body());
    }


}
