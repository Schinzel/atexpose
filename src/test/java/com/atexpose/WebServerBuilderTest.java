package com.atexpose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author schinzel
 */
public class WebServerBuilderTest {
    private WebServerBuilder mWsb;


    @Before
    public void before() {
        mWsb = AtExpose.create().getWebServerBuilder();
    }


    @Test
    public void testConstructor() {
        //Test default variables
        assertEquals(5555, mWsb.mPort);
        assertEquals(10, mWsb.mNoOfThreads);
        assertEquals(1, mWsb.mAccessLevel);
        assertEquals("web", mWsb.mWebServerDir);
        assertEquals(300, mWsb.mTimeout);
        assertEquals(1200, mWsb.mBrowserCacheMaxAge);
        assertEquals(false, mWsb.mForceHttps);
        assertEquals(true, mWsb.mUseCachedFiles);
        assertNotNull(mWsb.mServerSideVariables);
    }


    @Test
    public void testPort() {
        mWsb.port(77);
        assertEquals(77, mWsb.mPort);
    }


    @Test
    public void testNumberOfThreads() {
        mWsb.numberOfThreads(66);
        assertEquals(66, mWsb.mNoOfThreads);
    }


    @Test
    public void testWebServerDir() {
        mWsb.webServerDir("thisIsADir");
        assertEquals("thisIsADir", mWsb.mWebServerDir);
    }


    @Test
    public void testAccessLevel() {
        mWsb.accessLevel(2);
        assertEquals(2, mWsb.mAccessLevel);
    }


    @Test
    public void testTimeoutInMillis() {
        mWsb.timeoutInMillis(777);
        assertEquals(777, mWsb.mTimeout);
    }


    @Test
    public void testCacheMaxAgeInSeconds() {
        mWsb.browserCacheMaxAge(888);
        assertEquals(888, mWsb.mBrowserCacheMaxAge);
    }


    @Test
    public void testForceHttps() {
        mWsb.forceHttps(true);
        assertEquals(true, mWsb.mForceHttps);
        mWsb.forceHttps(false);
        assertEquals(false, mWsb.mForceHttps);
        mWsb.forceHttps(true);
        assertEquals(true, mWsb.mForceHttps);
    }


    @Test
    public void testUseCachedFiles() {
        mWsb.cacheFilesInRAM(true);
        assertEquals(true, mWsb.mUseCachedFiles);
        mWsb.cacheFilesInRAM(false);
        assertEquals(false, mWsb.mUseCachedFiles);
        mWsb.cacheFilesInRAM(true);
        assertEquals(true, mWsb.mUseCachedFiles);
    }


    @Test
    public void testServerSideVariables() {
        mWsb.addServerSideVar("a_key", "a_value");
        mWsb.addServerSideVar("b_key", "b_value");
        mWsb.addServerSideVar("c_key", "c_value");
        assertEquals("a_value", mWsb.mServerSideVariables.get("a_key"));
        assertEquals("b_value", mWsb.mServerSideVariables.get("b_key"));
        assertEquals("c_value", mWsb.mServerSideVariables.get("c_key"));
    }


    @Test
    public void testDefaultHtmlPage() {
        mWsb.defaultHtmlPage("monkey.html");
        assertEquals("monkey.html", mWsb.mDefaultPage);
    }


    @Test
    public void testForceDefaultPage() {
        mWsb.forceDefaultPage(true);
        assertTrue(mWsb.mForceDefaultPage);
        mWsb.forceDefaultPage(false);
        assertFalse(mWsb.mForceDefaultPage);
        mWsb.forceDefaultPage(true);
        assertTrue(mWsb.mForceDefaultPage);
    }

}
