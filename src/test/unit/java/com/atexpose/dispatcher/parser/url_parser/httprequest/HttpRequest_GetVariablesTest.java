package com.atexpose.dispatcher.parser.url_parser.httprequest;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class HttpRequest_GetVariablesTest {


    @Test
    public void getVariables_PostRequestRequest() {
        Map<String, String> variables = new HttpRequest(HttpRequestsUtil.POST_REQUEST_NORMAL).getVariables();
        assertThat(variables)
                .containsEntry("name", "John")
                .containsEntry("time", "2pm")
                .hasSize(2);
    }


    @Test
    public void getVariables_GetRequest() {
        Map<String, String> variables = new HttpRequest(HttpRequestsUtil.GET_REQUEST_NORMAL).getVariables();
        assertThat(variables)
                .containsEntry("SSN", "197107282222")
                .containsEntry("Pin", "88889")
                .hasSize(2);
    }


    @Test
    public void getVariables_GetRequestOneVariable_GetRequest() {
        Map<String, String> variables = new HttpRequest(HttpRequestsUtil.GET_REQUEST_ONE_VARIABLE).getVariables();
        assertThat(variables)
                .containsEntry("SSN", "197107282222")
                .hasSize(1);
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
}
