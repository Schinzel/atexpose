package com.atexpose.dispatcher.wrapper;

import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.substring.SubString;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


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
    public void setServerIncludeFiles_TwoIncludes_IncludesAdded() {
        String htmlPage = "<html><head><!--#include file=\"inc1.inc\" --></head><body><!--#include file=\"inc2.inc\" --><br></body></html>";
        String expected = "<html><head>ThisIsIncludeFile1</head><body>ThisIsIncludeFile2<br></body></html>";
        byte[] htmlPageAsByteArr = UTF8.getBytes(htmlPage);
        byte[] resultAsByteArr = WebWrapper.setServerIncludeFiles(htmlPageAsByteArr, "includefiles/");
        String result = UTF8.getString(resultAsByteArr);
        assertThat(result).isEqualTo(expected);
    }


    @Test
    public void setServerIncludeFiles_NoIncludes_IncludesAdded() {
        String htmlPage = "<html><head></head><body><h1>Header</h1><br></body></html>";
        byte[] htmlPageAsByteArr = UTF8.getBytes(htmlPage);
        byte[] resultAsByteArr = WebWrapper.setServerIncludeFiles(htmlPageAsByteArr, "includefiles/");
        String result = UTF8.getString(resultAsByteArr);
        assertThat(result).isEqualTo(htmlPage);
    }


    @Test
    public void setServerIncludeFiles_IncludesWithIncludes_IncludesAdded() {
        String htmlPage = "111 <!--#include file=\"include_with_includes.inc\" --> 111";
        String expected = "111 222 ThisIsIncludeFile1 ThisIsIncludeFile2 333 ThisIsIncludeFile2 333 222 111";
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
    public void wrapFile__CacheEnabledRequestDefaultFile3Times_CacheHas2Hits() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("");
        webWrapper.wrapFile("");
        webWrapper.wrapFile("");
        long cacheHits = webWrapper.mFileCache.cacheHits();
        assertThat(cacheHits).isEqualTo(2);
    }


    @Test
    public void wrapFile__CacheEnabledRequestSameFile3Times_CacheHas2Hits() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefile.html");
        webWrapper.wrapFile("somefile.html");
        webWrapper.wrapFile("somefile.html");
        long cacheHits = webWrapper.mFileCache.cacheHits();
        assertThat(cacheHits).isEqualTo(2);
    }


    @Test
    public void wrapFile__CacheEnabledRequest3DifferentFiles_CacheHas3Files() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(true)
                .build();
        webWrapper.wrapFile("somefile.html");
        webWrapper.wrapFile("somefile.html");
        webWrapper.wrapFile("somefile.js");
        webWrapper.wrapFile("monkey.jpg");
        int cacheSize = webWrapper.mFileCache.cacheSize();
        assertThat(cacheSize).isEqualTo(3);
    }


    @Test
    public void wrapFile__CacheDisabled_CacheEmpty() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(false)
                .build();
        webWrapper.wrapFile("somefile.html");
        webWrapper.wrapFile("somefile.js");
        webWrapper.wrapFile("monkey.jpg");
        int cacheSize = webWrapper.mFileCache.cacheSize();
        assertThat(cacheSize).isZero();
    }


    @Test
    public void wrapJson_Json_BodyIsJson() {
        JSONObject json = new JSONObject()
                .put("k1", "v1")
                .put("k2", "v2");
        String response = WebWrapper.builder()
                .webServerDir("testfiles/")
                .cacheFilesInRam(false)
                .build()
                .wrapJSON(json.toString());
        String body = SubString.create(response)
                .startDelimiter("\r\n\r\n")
                .toString();
        assertThat(body).startsWith(json.toString());
    }


    @Test
    public void wrapJSON_CustomHeader_HeaderContainsCustomHeader() {
        JSONObject jo = new JSONObject()
                .put("k1", "v1");
        Map<String, String> customHeaders = ImmutableMap.<String, String>builder()
                .put("bear", "kodiak")
                .build();
        String response = WebWrapper.builder()
                .webServerDir("testfiles/")
                .responseHeaders(customHeaders)
                .build()
                .wrapJSON(jo.toString());
        String header = SubString.create(response)
                .endDelimiter("\r\n\r\n")
                .toString();
        assertThat(header).contains("bear: kodiak");
    }


    @Test
    public void getFileHeaderAndContent_NonExistingFile_Expcetion() {
        WebWrapper webWrapper = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                webWrapper.getFileHeaderAndContent("nonexistingfile.html")
        );
    }


    @Test
    public void wrapFile_HtmlFile_HtmlFile() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("somefile.html");
        assertThat(UTF8.getString(file))
                .contains("<div>This is somefile.html</div>");
    }


    @Test
    public void wrapFile_JsFile_JsFile() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("somefile.js");
        assertThat(UTF8.getString(file))
                .contains("function monkey()");
    }


    @Test
    public void wrapFile_IndexFile_IndexFile() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("index.html");
        assertThat(UTF8.getString(file))
                .contains("<div>Success</div>");
    }


    @Test
    public void wrapFile_EmptyFileName_IndexFile() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("");
        assertThat(UTF8.getString(file))
                .contains("<div>Success</div>");
    }


    @Test
    public void wrapFile_DirNameWithEndSlash_IndexFileInDir() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("somefolder");
        assertThat(UTF8.getString(file))
                .contains("<div>Index.html in somefolder dir</div>");
    }


    @Test
    public void wrapFile_DirNameWithNoEndSlash_IndexFileInDir() {
        byte[] file = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("somefolder");
        assertThat(UTF8.getString(file))
                .contains("<div>Index.html in somefolder dir</div>");
    }


    @Test
    public void wrapFile_NonExistingFile_404Page() {
        byte[] html404PageAsBytes = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("nonexistingfile.html");
        String html404Page = UTF8.getString(html404PageAsBytes);
        assertThat(html404Page)
                .contains("<html><body><center>File not found</center><body></html>");
    }


    @Test
    public void wrapFile_Jpg_ReturnsFile() {
        byte[] ab = WebWrapper.builder()
                .webServerDir("testfiles/")
                .build()
                .wrapFile("monkey.jpg");
        String httpResponse = UTF8.getString(ab);
        String contentLength = SubString.create(httpResponse)
                .startDelimiter("Content-Length: ")
                .endDelimiter("\r\n")
                .getString();
        assertThat(contentLength).isEqualTo("416176");
    }



    @Test
    public void backslashDollarSigns_emptyString_emptyString(){
        String actual = WebWrapper.backslashDollarSigns("");
        assertThat(actual).isEmpty();
    }

    @Test
    public void backslashDollarSigns_null_exception(){
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                WebWrapper.backslashDollarSigns(null)
        );

    }


    @Test
    public void backslashDollarSigns_noDollarSings_unchanged(){
        String actual = WebWrapper.backslashDollarSigns("any_string");
        assertThat(actual).isEqualTo("any_string");
    }

    @Test
    public void backslashDollarSigns_onlyDollarSign_fixed(){
        String actual = WebWrapper.backslashDollarSigns("$");
        char ch[]={'\\', '$'};
        String expected = new String(ch);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void backslashDollarSigns_severalDollarSigns_allFixed(){
        String actual = WebWrapper.backslashDollarSigns("$a$b$c$");
        char ch[]={'\\', '$','a','\\', '$','b','\\', '$','c','\\', '$'};
        String expected = new String(ch);
        assertThat(actual).isEqualTo(expected);
    }

}
