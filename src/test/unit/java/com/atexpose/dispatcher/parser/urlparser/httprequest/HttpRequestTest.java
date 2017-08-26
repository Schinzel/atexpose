/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;


public class HttpRequestTest {


    @Test
    public void getUri() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER);
        URI uri = request.getURI();
        assertEquals("http://127.0.0.1:5555/index.html?xyz=1234", uri.toString());
        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(5555, uri.getPort());
        assertEquals("xyz=1234", uri.getQuery());
        assertEquals("/index.html", uri.getPath());
    }


    @Test
    public void getUri_HttpHeaderNoQueryString_UriToStringNoQuestionMarkAtEndOfUrl() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER_NO_QUERY_STRING);
        assertEquals("http://127.0.0.1:5555/index.html", request.getURI().toString());
    }


    @Test
    public void getUri_HttpHeaderNoQueryString_QueryEmptyString() {
        HttpRequest request = new HttpRequest(HttpRequestsUtil.HTTP_HEADER_NO_QUERY_STRING);
        assertEquals(null, request.getURI().getQuery());
    }


    @Test
    public void getPath_PostRequestNormal_Path() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_GetRequestNormal_Path() {
        String path = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_GetRequestNoVariablesButWithQuestionMark_Path() {
        String path = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getPath();
        assertThat(path).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getPath_PostRequestLongMethodName() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_LONG_METHOD_NAME).getPath();
        assertThat(path).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz");
    }


    @Test
    public void getPath_PostRequestShortMethodName() {
        String path = new HttpRequest(HttpRequestsUtil.POST_REQUEST_SHORT_METHOD_NAME).getPath();
        assertThat(path).isEqualTo("call/a");
    }


    @Test
    public void getUrl_PostRequestNormal_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM");
    }


    @Test
    public void getUrl_PostRequestShortMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_SHORT_METHOD_NAME).getURL();
        assertThat(url).isEqualTo("call/a");
    }


    @Test
    public void getUrl_PostRequestLongMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.POST_REQUEST_LONG_METHOD_NAME).getURL();
        assertThat(url).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz");
    }


    @Test
    public void getUrl_GetRequestNormal_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM?SSN=197107282222&Pin=88889");
    }


    @Test
    public void getUrl_GetRequestNoVarsButWithQuestionMake_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getURL();
        assertThat(url).isEqualTo("call/getDataFromPM?");
    }


    @Test
    public void getUrl_GetRequestShortMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_SHORT_METHODNAME).getURL();
        assertThat(url).isEqualTo("call/a?SSN=197107282222");
    }


    @Test
    public void getUrl_GetRequestLongMethodName_Url() {
        String url = new HttpRequest(HttpRequestsUtil.GET_REQUEST_LONG_METHODNAME).getURL();
        assertThat(url).isEqualTo("call/abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz_abcdefghijklmonpqrstuvxyz?SSN=197107282222");
    }


    @Test
    public void getVariables_PostRequestRequest() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getVariables();
        assertThat(actual).extracting("name").contains("John");
        assertThat(actual).extracting("time").contains("2pm");
        assertThat(actual).hasSize(2);
    }


    @Test
    public void getVariables_GetRequest() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getVariables();
        assertThat(actual).extracting("SSN").contains("197107282222");
        assertThat(actual).extracting("Pin").contains("88889");
        assertThat(actual).hasSize(2);
    }


    @Test
    public void getVariables_GetRequestOneVariable_GetRequest() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.GET_REQUEST_ONE_VARIABLE).getVariables();
        assertThat(actual).extracting("SSN").contains("197107282222");
        assertThat(actual).hasSize(1);
    }


    @Test
    public void getVariables_PostRequestNoArgsNormal_EmptyMap() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_ARGS).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getVariables_PostRequestNoArgsOneLineBreak_EmptyMap() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.POST_REQUEST_ONE_LINEBREAK).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getVariables_PostRequestNoArgsNoLineBreaks_EmptyMap() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_LINE_BREAKS).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getVariables_GetRequestNoVariablesButWithQuestionMark_EmptyMap() {
        Map<String, String> actual = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NO_VARIABLES_BUT_WITH_QUESTION_MARK).getVariables();
        assertThat(actual).isEmpty();
    }


    @Test
    public void getBody_PostRequest_Body() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("name=John&time=2pm");
    }


    @Test
    public void getBody_PostRequestNoArguments_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_ARGS).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_PostRequestOneLineBreakBetweenHeaderAndBody_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_ONE_LINEBREAK).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_PostRequestNoLineBreakBetweenHeaderAndBody_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NO_LINE_BREAKS).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_GetRequest_Body() {
        String body = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getBody_GetRequestNoVariablesButWithQuestionMark_EmptyString() {
        String body = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getBody();
        assertThat(body).isEqualTo("");
    }


    @Test
    public void getHeaderValue_Pragma_NoCache() {
        String headerValue = new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("Pragma");
        assertThat(headerValue).isEqualTo("no-cache");
    }


    @Test
    public void getHeaderValue_NonExistingHeader_EmptyString() {
        String headerValue = new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("I_do_not_exist");
        assertThat(headerValue).isEqualTo("");
    }


    @Test
    public void getHeaderValue_EmptyHeaderName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("")
        );
    }


    @Test
    public void getHeaderValue_NullHeaderName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue(null)
        );
    }


    @Test
    public void getHeaderValue_EmptyHeaderValue_EmptyString() {
        String headerValue = new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("Pragma2");
        assertThat(headerValue).isEqualTo("");
    }


    @Test
    public void getHeaderValue_LongHeaderValue_HeaderValue() {
        String headerValue = new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("Cookie");
        assertThat(headerValue).isEqualTo("db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0");
    }


    @Test
    public void getHeaderValue_Host_LocalHost() {
        String headerValue = new HttpRequest(HttpRequestsUtil.HTTP_HEADER).getHeaderValue("Host");
        assertThat(headerValue).isEqualTo("127.0.0.1:5555");
    }


}
