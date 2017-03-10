package com.atexpose.dispatcher.wrapper.webresponse;

import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import com.atexpose.util.EncodingUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import io.schinzel.basicutils.collections.Cache;
import lombok.val;
import org.json.JSONObject;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import io.schinzel.basicutils.EmptyObjects;

/**
 * @author schinzel
 */
public class WebWrapperTest {

    @Test
    public void testSetServerSideVariables() {
        String htmlPage = "<html><head><##=VaRiAbLe##></head><body><##=variable##><br><##=VARIABLE##></body></html>";
        String expected = "<html><head>var1</head><body>var2<br>var3</body></html>";
        byte[] htmlPageAsByteArr = EncodingUtil.convertToByteArray(htmlPage);
        Map<String, String> ssv = new HashMap<>();
        ssv.put("VaRiAbLe", "var1");
        ssv.put("variable", "var2");
        ssv.put("VARIABLE", "var3");
        byte[] resultAsByteArr = WebWrapper.setServerSideVariables(htmlPageAsByteArr, ssv);
        String result = EncodingUtil.convertToString(resultAsByteArr);
        assertEquals(expected, result);
    }


    @Test
    public void testSetServerSideVariables2() {
        String htmlPage = "<##=first##><html><head></head><body><##=middle##></body></html><##=last##>";
        String expected = "var1<html><head></head><body>var3</body></html>var2";
        byte[] htmlPageAsByteArr = EncodingUtil.convertToByteArray(htmlPage);
        Map<String, String> ssv = new HashMap<>();
        ssv.put("first", "var1");
        ssv.put("last", "var2");
        ssv.put("middle", "var3");
        byte[] resultAsByteArr = WebWrapper.setServerSideVariables(htmlPageAsByteArr, ssv);
        String result = EncodingUtil.convertToString(resultAsByteArr);
        assertEquals(expected, result);
    }


    @Test
    public void setServerIncludeFiles() {
        String htmlPage = "<html><head><##inc1.inc##></head><body><##inc2.inc##><br></body></html>";
        String expected = "<html><head>ThisIsIncludeFile1</head><body>ThisIsIncludeFile2<br></body></html>";
        byte[] htmlPageAsByteArr = EncodingUtil.convertToByteArray(htmlPage);
        byte[] resultAsByteArr = WebWrapper.setServerIncludeFiles(htmlPageAsByteArr, "includefiles/");
        String result = EncodingUtil.convertToString(resultAsByteArr);
        assertEquals(expected, result);
    }


    @Test
    public void testCache() {
        int cacheSecs = 10;
        boolean useCachedFiles = true;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        byte[] file1 = webWrapper.wrapFile("read_from_cache.html");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        byte[] file2 = webWrapper.wrapFile("read_from_cache.html");
        assertEquals(1, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        byte[] file3 = webWrapper.wrapFile("read_another_cached.html");
        assertEquals(1, filesCache.cacheHits());
        assertEquals(2, filesCache.cacheSize());
        byte[] file4 = webWrapper.wrapFile("read_another_cached.html");
        assertEquals(2, filesCache.cacheHits());
        assertEquals(2, filesCache.cacheSize());
        String str2 = EncodingUtil.convertToString(file2);
        String str4 = EncodingUtil.convertToString(file4);
        assertTrue(str2.contains("<div>Hello</div>"));
        assertTrue(str4.contains("<div>Hello</div>"));
        Assert.assertArrayEquals(file1, file2);
        Assert.assertArrayEquals(file3, file4);
        JSONObject statusAsJson = webWrapper.getState().getJson();
        assertEquals("testfiles/", statusAsJson.getString("Directory"));
        assertEquals(10, statusAsJson.getInt("BrowserCacheMaxAge"));
    }


    @Test
    public void testCacheDefault() {
        int cacheSecs = 10;
        boolean useCachedFiles = true;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        //These should hit index.html every time
        webWrapper.wrapFile("");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile(null);
        assertEquals(1, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile("");
        assertEquals(2, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile(null);
        assertEquals(3, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
    }


    @Test
    public void testCacheDefault_staticFiles() {
        int cacheSecs = 10;
        boolean useCachedFiles = true;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        //These should hit index.html every time
        webWrapper.wrapFile("monkey.jpg");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile("monkey.jpg");
        assertEquals(1, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile("monkey.jpg");
        assertEquals(2, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
        webWrapper.wrapFile("monkey.jpg");
        assertEquals(3, filesCache.cacheHits());
        assertEquals(1, filesCache.cacheSize());
    }


    @Test
    public void testNoCache() {
        int cacheSecs = 10;
        boolean useCachedFiles = false;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        webWrapper.wrapFile("read_from_cache.html");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        webWrapper.wrapFile(null);
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        webWrapper.wrapFile("read_another_cached.html");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
        webWrapper.wrapFile("");
        assertEquals(0, filesCache.cacheHits());
        assertEquals(0, filesCache.cacheSize());
    }


    @Test
    public void testForcedDefault() {
        String url = "http://www.example.com/subdir/index.html";
        int cacheSecs = 10;
        boolean useCachedFiles = false;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        String result = webWrapper.wrapRedirect(url, RedirectHttpStatus.TEMPORARY);
        String expected = "HTTP/1.1 302\r\n"
                + "Server: AtExpose\r\n"
                + "Content-Length: 0\r\n"
                + "Location: http://www.example.com/subdir/index.html\r\n\r\n";
        assertEquals(expected, result);
    }


    @Test
    public void testWrapJSON() {
        JSONObject jo = new JSONObject();
        jo.put("k1", "v1");
        jo.put("k2", "v2");
        int cacheSecs = 10;
        boolean useCachedFiles = false;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, EmptyObjects.EMPTY_MAP);
        String result = webWrapper.wrapJSON(jo);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: AtExpose\r\n"
                + "Content-Length: 21\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n\r\n"
                + "{\"k1\":\"v1\",\"k2\":\"v2\"}\n\n";
        assertEquals(expected, result);
    }


    @Test
    public void testCustomResponseHeaders() {
        JSONObject jo = new JSONObject();
        jo.put("k1", "v1");
        jo.put("k2", "v2");
        int cacheSecs = 10;
        boolean useCachedFiles = false;
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("monkey", "gibbon");
        responseHeaders.put("bear", "kodiak");
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, EmptyObjects.EMPTY_STRING, false, null, responseHeaders);
        String result = webWrapper.wrapJSON(jo);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: AtExpose\r\n"
                + "Content-Length: 21\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "monkey: gibbon\r\n"
                + "bear: kodiak\r\n"
                + "Cache-Control: max-age=0\r\n\r\n"
                + "{\"k1\":\"v1\",\"k2\":\"v2\"}\n\n";
        assertEquals(expected, result);
    }


    @Test
    public void testForceDefault() {
        int cacheSecs = 10;
        boolean useCachedFiles = false;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, useCachedFiles, "forced_file.html", true, null, EmptyObjects.EMPTY_MAP);
        byte[] resultAsByte = webWrapper.wrapFile("index.html");
        String resultAsString = EncodingUtil.convertToString(resultAsByte);
        assertTrue(resultAsString.contains("<div>This file is forced when asking for any file</div>"));
    }


    @Test
    public void testWrapFileHtml() {
        String path = "somefile.html";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the requested file and not the default
        assertTrue(filesCache.has("testfiles/somefile.html"));
    }


    @Test
    public void testWrapFileJs() {
        String path = "somefile.js";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the requested file and not the default
        assertTrue(filesCache.has("testfiles/somefile.js"));
    }


    @Test
    public void testWrapFileDefault() {
        String path = "";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the default file
        assertTrue(filesCache.has("testfiles/index.html"));
    }


    @Test
    public void testWrapFileEmpty() {
        String path = "";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the default file
        assertTrue(filesCache.has("testfiles/default.html"));
    }


    @Test
    public void testWrapFileEmptyFolderSlash() {
        String path = "somefolder/";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the default file
        assertTrue(filesCache.has("testfiles/somefolder/default.html"));
    }


    @Test
    public void testWrapFileEmptyFolderNoSlash() {
        String path = "somefolder";
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        Cache<String, byte[]> filesCache = webWrapper.mFilesCache;
        webWrapper.wrapFile(path);
        // should return the default file
        assertTrue(filesCache.has("testfiles/somefolder/default.html"));
    }


    @Test
    public void testFolderPath() {
        int cacheSecs = 10;
        val webWrapper = new WebWrapper("testfiles/", cacheSecs, true, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        boolean test1 = webWrapper.isFolderPath("somefolder");
        assertTrue(test1);
        boolean test2 = webWrapper.isFolderPath("/somefolder");
        assertTrue(test2);
        boolean test3 = webWrapper.isFolderPath("/somefolder/");
        assertTrue(test3);
        boolean test4 = webWrapper.isFolderPath("somefolder.js");
        assertFalse(test4);
        boolean test5 = webWrapper.isFolderPath("/somefolder.html");
        assertFalse(test5);
    }


    @Test
    public void testIsTextFile() {
        assertTrue(WebWrapper.isTextFile("m.html"));
        assertTrue(WebWrapper.isTextFile("really_really_really_long_file_name.html"));
        assertTrue(WebWrapper.isTextFile("dot.in.name.html"));
        assertTrue(WebWrapper.isTextFile("m.HTML"));
        assertTrue(WebWrapper.isTextFile("m.hTmL"));
        //
        assertTrue(WebWrapper.isTextFile("file.htm"));
        assertTrue(WebWrapper.isTextFile("file.css"));
        assertTrue(WebWrapper.isTextFile("file.js"));
        assertTrue(WebWrapper.isTextFile("file.txt"));
        //
        assertFalse(WebWrapper.isTextFile("file.ico"));
        assertFalse(WebWrapper.isTextFile("file.png"));
        assertFalse(WebWrapper.isTextFile("file.jpg"));
        assertFalse(WebWrapper.isTextFile("file.jpeg"));
        assertFalse(WebWrapper.isTextFile("file.gif"));
        assertFalse(WebWrapper.isTextFile("file.svg"));
        assertFalse(WebWrapper.isTextFile("file.map"));
    }


    @Test
    public void test_getTextFileHeaderAndContent() {
        val webWrapper = new WebWrapper("testfiles/", 10, false, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        byte[] ab = webWrapper.getTextFileHeaderAndContent("nonexistingfile.html");
        String str = EncodingUtil.convertToString(ab);
        assertTrue(str.contains("File 'nonexistingfile.html' not found"));
    }


    @Test
    public void test_getTextFileContent() {
        val webWrapper = new WebWrapper("testfiles/", 10, false, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        byte[] ab = webWrapper.getTextFileContent("nonexistingfile.html");
        Assert.assertNull(ab);
    }


    @Test
    public void test_getStaticFileHeaderAndContent_nonExistingFile() {
        val webWrapper = new WebWrapper("testfiles/", 10, false, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        byte[] ab = webWrapper.getStaticFileHeaderAndContent("nonexistingfile.jpg");
        String str = EncodingUtil.convertToString(ab);
        assertTrue(str.contains("File 'nonexistingfile.jpg' not found"));
    }


    @Test
    public void test_getStaticFileHeaderAndContent() {
        val webWrapper = new WebWrapper("testfiles/", 10, false, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        //Read file
        String fileName = webWrapper.getActualFilename("monkey.jpg");
        byte[] ab = webWrapper.getStaticFileHeaderAndContent(fileName);
        //Get header
        String partOfHeader = new String(ab, 0, 70, Charset.forName("UTF-8"));
        //Get conent length in header
        String conentLengthHeader = "Content-Length: ";
        int start = partOfHeader.indexOf(conentLengthHeader) + conentLengthHeader.length();
        int end = partOfHeader.indexOf("\r\n", start);
        String lengthAsString = partOfHeader.substring(start, end);
        int contentLengthInHeader = Integer.parseInt(lengthAsString);
        //Check that conent length in header is same as size of actual file
        assertEquals(416176, contentLengthInHeader);
    }


    @Test
    public void test_wrapFile_staticFile() {
        val webWrapper = new WebWrapper("testfiles/", 10, false, "default.html", false, null, EmptyObjects.EMPTY_MAP);
        //Read file
        byte[] ab = webWrapper.wrapFile("monkey.jpg");
        //Get header
        String partOfHeader = new String(ab, 0, 70, Charset.forName("UTF-8"));
        //Get conent length in header
        String conentLengthHeader = "Content-Length: ";
        int start = partOfHeader.indexOf(conentLengthHeader) + conentLengthHeader.length();
        int end = partOfHeader.indexOf("\r\n", start);
        String lengthAsString = partOfHeader.substring(start, end);
        int contentLengthInHeader = Integer.parseInt(lengthAsString);
        //Check that conent length in header is same as size of actual file
        assertEquals(416176, contentLengthInHeader);
    }

}
