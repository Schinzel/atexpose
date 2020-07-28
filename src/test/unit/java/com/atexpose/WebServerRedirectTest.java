package com.atexpose;

import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.WebServerBuilder;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebServerRedirectTest {
    IDispatcher mWebServer;


    @After
    public void after() {
        mWebServer.shutdown();
        //Snooze required to get tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void WebServerRedirect_PageInRoot_ShouldRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
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
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
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
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
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
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://127.0.0.1:5555/dirdest/dest.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_RequestFromFromHost_ShouldRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addHostRedirect("127.0.0.1", "localhost")
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:5555/dir1/dir2/src.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_HttpsRedirectSetUp_HttpRequest_ShouldRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .addHostRedirect("127.0.0.1", "localhost")
                .forceHttps(true)
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dir1/dir2/src.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_ComboRedirectSetUp_ShouldRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .forceHttps(true)
                .addHostRedirect("127.0.0.1", "localhost")
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/dirdest/dest.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_FailWhaleDoNotRedirect_ShouldNotRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .webServerDir("testfiles/")
                .numberOfThreads(5)
                .setFailWhaleRedirect("monkey.html", false)
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/index.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(200, response.statusCode());
    }


    @Test
    public void WebServerRedirect_HttpsDoNotRedirect_ShouldNotRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .webServerDir("testfiles/")
                .numberOfThreads(5)
                .forceDefaultPage(false)
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/index.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(200, response.statusCode());
    }


    @Test
    public void WebServerRedirect_FailWhailRedirectInComboWithOtherRedirects_ShouldRedirectToHostHttpsAndFailWhalePageWithQueriesIntact() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .setFailWhaleRedirect("monkey.html")
                .forceHttps(true)
                .addHostRedirect("127.0.0.1", "localhost")
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(302, response.statusCode());
        assertEquals("https://localhost:5555/monkey.html?key2=val2", response.header("Location"));
    }


    @Test
    public void WebServerRedirect_MethodCall_ShouldNotRedirect() throws Exception {
        mWebServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .forceHttps(true)
                .addHostRedirect("127.0.0.1", "localhost")
                .build();
        AtExpose.create()
                .start(mWebServer);
        Connection.Response response = Jsoup
                .connect("http://127.0.0.1:5555/api/ping")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        assertEquals(200, response.statusCode());
        assertEquals("pong", response.body());
    }


}
