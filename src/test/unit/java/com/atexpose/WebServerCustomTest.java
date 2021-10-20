package com.atexpose;

import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.WebServerBuilder;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;


/**
 * Tests different set ups of the web server.
 *
 * @author Schinzel
 */

public class WebServerCustomTest {
    private AtExpose mAtExpose;


    @After
    public void after() {
        mAtExpose.shutdown();
        //Snooze required to get tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void requestPage_PageDoesNotExistCustom404_Custom404() throws IOException {
        IDispatcher webServer = WebServerBuilder.create()
                //Set dir where html and other files resides
                .webServerDir("testfiles")
                //Set custom 404 page
                .fileName404Page("WebServerCustomTest_404Page.html")
                .webServerDir("testfiles")
                //Build web server
                .build();
        mAtExpose = AtExpose.create()
                //Start web server
                .start(webServer);
        String result = Jsoup
                .connect("http://127.0.0.1:5555/noSuchFile.html")
                .method(Connection.Method.GET)
                .ignoreHttpErrors(true)
                .execute()
                .body();
        assertThat(result).contains("Custom 404. File not found");
    }


    @Test
    public void requestPage_PageDoesNotExistNotCustom404_Default404() throws IOException {
        IDispatcher webServer = WebServerBuilder.create()
                //Set dir where html and other files resides
                .webServerDir("testfiles")
                //Build web server
                .build();
        mAtExpose = AtExpose.create()
                //Start web server
                .start(webServer);
        String result = Jsoup
                .connect("http://127.0.0.1:5555/noSuchFile.html")
                .method(Connection.Method.GET)
                .ignoreHttpErrors(true)
                .execute()
                .body();
        assertThat(result).contains("<html><body><center>File not found</center><body></html>");
    }
}
