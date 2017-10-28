package com.atexpose.dispatcher.wrapper;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.collections.Cache;
import io.schinzel.basicutils.substring.SubString;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


/**
 * @author schinzel
 */
public class WebWrapperTest {

    @Test
    public void testSetServerSideVariables() {
        String htmlPage = "<html><head><!--#echo var=\"VaRiAbLe\" --></head><body><!--#echo var=\"variable\" --><br><!--#echo var=\"VARIABLE\" --></body></html>";
        byte[] htmlPageAsByteArr = UTF8.getBytes(htmlPage);
        Map<String, String> ssv = ImmutableMap.<String, String>builder()
                .put("VaRiAbLe", "var1")
                .put("variable", "var2")
                .put("VARIABLE", "var3")
                .build();
        byte[] resultAsByteArr = WebWrapper.setServerSideVariables(htmlPageAsByteArr, ssv);
        String result = UTF8.getString(resultAsByteArr);
        assertThat(result).isEqualTo("<html><head>var1</head><body>var2<br>var3</body></html>");
    }


    @Test
    public void testSetServerSideVariables2() {
        String htmlPage = "<!--#echo var=\"first\" --><html><head></head><body><!--#echo var=\"middle\" --></body></html><!--#echo var=\"last\" -->";
        String expected = "var1<html><head></head><body>var3</body></html>var2";
        byte[] htmlPageAsByteArr = UTF8.getBytes(htmlPage);
        Map<String, String> ssv = ImmutableMap.<String, String>builder()
                .put("first", "var1")
                .put("last", "var2")
                .put("middle", "var3")
                .build();
        byte[] resultAsByteArr = WebWrapper.setServerSideVariables(htmlPageAsByteArr, ssv);
        String result = UTF8.getString(resultAsByteArr);
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void setServerIncludeFiles() {
        String htmlPage = "<html><head><!--#include file=\"inc1.inc\" --></head><body><!--#include file=\"inc2.inc\" --><br></body></html>";
        String expected = "<html><head>ThisIsIncludeFile1</head><body>ThisIsIncludeFile2<br></body></html>";
        byte[] htmlPageAsByteArr = UTF8.getBytes(htmlPage);
        byte[] resultAsByteArr = WebWrapper.setServerIncludeFiles(htmlPageAsByteArr, "includefiles/");
        String result = UTF8.getString(resultAsByteArr);
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void readFile_FileHasIncludeFiles_IncludeStringShouldBeReplacedWithFile() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        byte[] file = webWrapper.wrapFile("with_include_file.html");
        String fileAsString = UTF8.getString(file);
        //The include file command should not be in read file
        assertThat(fileAsString).doesNotContain("<!--#include file=\"inc_file.inc\" -->");
        //The content of the include file should be in the read file
        assertThat(fileAsString).contains("<b>This is an include file</b>");
    }


    @Test
    public void readFile_FileHasServerSideVar_VaribleShouldBeReplacedWithValue() {
        Map<String, String> serverSideVars = ImmutableMap.<String, String>builder()
                .put("MY_VAR", "this_is_an_inserted_value")
                .build();
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .serverSideVariables(serverSideVars)
                .cacheFilesInRam(false)
                .build();
        byte[] file = webWrapper.wrapFile("with_server_side_var.html");
        String fileAsString = UTF8.getString(file);
        //The server side variable reference should not be inte the file
        assertThat(fileAsString).doesNotContain("MY_VAR");
        //The value of the server side value should be in the file
        assertThat(fileAsString).contains("this_is_an_inserted_value");
    }


    @Test
    public void readFile_FileHasIncludeFileWhichHasServerSideVars_TheServerSideVarsShouldBePresent() {
        Map<String, String> serverSideVars = ImmutableMap.<String, String>builder()
                .put("var1", "111")
                .put("var2", "222")
                .put("var3", "333")
                .build();
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .serverSideVariables(serverSideVars)
                .cacheFilesInRam(false)
                .build();
        byte[] file = webWrapper.wrapFile("with_inc_file_which_has_vars.html");
        String fileAsString = UTF8.getString(file);
        //The server side variable reference should not be inte the file
        assertThat(fileAsString).doesNotContain("<!--#include file=\"inc_with_include_vars.inc\" -->");
        //The value of the server side value should be in the file
        assertThat(fileAsString).contains("#START#111222333#END#");
    }


    @Test
    public void testCache() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(true)
                .build();
        Cache<String, byte[]> filesCache = webWrapper.getFilesCache();
        assertThat(filesCache.cacheHits()).isEqualTo(0);
        assertThat(filesCache.cacheSize()).isEqualTo(0);
        byte[] file1 = webWrapper.wrapFile("read_from_cache.html");
        assertThat(filesCache.cacheHits()).isEqualTo(0);
        assertThat(filesCache.cacheSize()).isEqualTo(1);
        byte[] file2 = webWrapper.wrapFile("read_from_cache.html");
        assertThat(filesCache.cacheHits()).isEqualTo(1);
        assertThat(filesCache.cacheSize()).isEqualTo(1);
        byte[] file3 = webWrapper.wrapFile("read_another_cached.html");
        assertThat(filesCache.cacheHits()).isEqualTo(1);
        assertThat(filesCache.cacheSize()).isEqualTo(2);
        byte[] file4 = webWrapper.wrapFile("read_another_cached.html");
        assertThat(filesCache.cacheHits()).isEqualTo(2);
        assertThat(filesCache.cacheSize()).isEqualTo(2);
        assertThat(UTF8.getString(file2)).contains("<div>Hello</div>");
        assertThat(UTF8.getString(file4)).contains("<div>Hello</div>");
        assertThat(file2).isEqualTo(file1);
        assertThat(file4).isEqualTo(file3);
        JSONObject statusAsJson = webWrapper.getState().getJson();
        assertThat(statusAsJson.getString("Directory")).isEqualTo("testfiles/");
        assertThat(statusAsJson.getInt("BrowserCacheMaxAge")).isEqualTo(10);
    }


    @Test
    public void testCacheDefault() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(true)
                .build();
        Cache<String, byte[]> filesCache = webWrapper.getFilesCache();
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
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(true)
                .build();
        Cache<String, byte[]> filesCache = webWrapper.getFilesCache();
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
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        Cache<String, byte[]> filesCache = webWrapper.getFilesCache();
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
    public void testWrapJSON() {
        JSONObject json = new JSONObject()
                .put("k1", "v1")
                .put("k2", "v2");
        String result = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build()
                .wrapJSON(json);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: @Expose\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n"
                + "Content-Length: 21\r\n\r\n"
                + "{\"k1\":\"v1\",\"k2\":\"v2\"}\n\n";
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void testCustomResponseHeaders() {
        JSONObject jo = new JSONObject();
        jo.put("k1", "v1");
        jo.put("k2", "v2");
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("monkey", "gibbon");
        responseHeaders.put("bear", "kodiak");
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .responseHeaders(responseHeaders)
                .build();
        String result = webWrapper.wrapJSON(jo);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: @Expose\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n"
                + "Content-Length: 21\r\n"
                + "monkey: gibbon\r\n"
                + "bear: kodiak\r\n\r\n"
                + "{\"k1\":\"v1\",\"k2\":\"v2\"}\n\n";
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void testWrapFileHtml() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefile.html");
        assertThat(webWrapper.getFilesCache().has("testfiles/somefile.html"))
                .isTrue();
    }


    @Test
    public void testWrapFileJs() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefile.js");
        // should return the requested file and not the default
        assertThat(webWrapper.getFilesCache().has("testfiles/somefile.js"))
                .isTrue();
    }


    @Test
    public void testWrapFileDefault() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("index.html");
        // should return the default file
        assertThat(webWrapper.getFilesCache().has("testfiles/index.html"))
                .isTrue();
    }


    @Test
    public void testWrapFileEmpty() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("");
        // should return the default file
        assertThat(webWrapper.getFilesCache().has("testfiles/index.html"))
                .isTrue();
    }


    @Test
    public void testWrapFileEmptyFolderSlash() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefolder/");
        // should return the default file
        assertThat(webWrapper.getFilesCache().has("testfiles/somefolder/index.html"))
                .isTrue();
    }


    @Test
    public void testWrapFileEmptyFolderNoSlash() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefolder");
        // should return the default file
        assertThat(webWrapper.getFilesCache().has("testfiles/somefolder/index.html"))
                .isTrue();
    }


    @Test
    public void test_getTextFileHeaderAndContent() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        byte[] html404PageAsBytes = webWrapper.getTextFileHeaderAndContent("nonexistingfile.html");
        String html404Page = UTF8.getString(html404PageAsBytes);
        assertThat(html404Page).contains("<html><body><center>File not found</center><body></html>");
    }


    @Test
    public void test_getTextFileContent() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        byte[] ab = webWrapper.getTextFileContent("nonexistingfile.html");
        assertThat(ab).isNull();
    }


    @Test
    public void test_getStaticFileHeaderAndContent_nonExistingFile() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        byte[] ab = webWrapper.getStaticFileHeaderAndContent("nonexistingfile.jpg");
        String str = UTF8.getString(ab);
        assertThat(str).contains("<html><body><center>File not found</center><body></html>");
    }


    @Test
    public void test_getStaticFileHeaderAndContent() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        //Read file
        String fileName = webWrapper.getActualFilename("monkey.jpg");
        byte[] ab = webWrapper.getStaticFileHeaderAndContent(fileName);
        String httpResponse = UTF8.getString(ab);
        String contentLength = SubString.create(httpResponse)
                .startDelimiter("Content-Length: ")
                .endDelimiter("\r\n")
                .getString();
        assertThat(contentLength).isEqualTo("416176");
    }


    @Test
    public void test_wrapFile_staticFile() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .browserCacheMaxAge(10)
                .cacheFilesInRam(false)
                .build();
        //Read file
        byte[] ab = webWrapper.wrapFile("monkey.jpg");
        String httpResponse = UTF8.getString(ab);
        String contentLength = SubString.create(httpResponse)
                .startDelimiter("Content-Length: ")
                .endDelimiter("\r\n")
                .getString();
        assertThat(contentLength).isEqualTo("416176");
    }

}
