package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WebServerRedirectTest {
    Dispatcher mWebServer;


    @Before
    public void before() {
        mWebServer = AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
    }


    @After
    public void after() {
        mWebServer.shutdown();
    }


    @Test
    public void testRedirect_pageInRoot() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dest.html", response.header("Location"));
    }


    @Test
    public void testRedirect_pageInSubDir() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html", response.header("Location"));
    }


    @Test
    public void testRedirect_pageInRoot_withQueryString() throws Exception {
        //Basic test
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/src.html?key1=val1")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dest.html?key1=val1", response.header("Location"));
    }


    @Test
    public void testRedirect_pageInSubDir_withQueryStriing() throws IOException {
        //Test that query strings are passed on with dirs
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html?key2=val2", response.header("Location"));
    }


}
