package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.parser.Request;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

public class UrlParserWithGSuiteAuthTest {
    private String HTTP_HEADER_WITH_AUTH_COOKIES =
            "POST /call/encryptSSN HTTP/1.1\r\r\n" +
                    "Host: kollektiva-admin.herokuapp.com\r\r\n" +
                    "Connection: close\r\n" +
                    "Accept: */*\r\n" +
                    "Origin: https://kollektiva-admin.herokuapp.com\r\n" +
                    "X-Requested-With: XMLHttpRequest\r\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "Dnt: 1\r\n" +
                    "Referer: https://kollektiva-admin.herokuapp.com/devops.html\r\n" +
                    "Accept-Encoding: gzip, deflate, br\r\n" +
                    "Accept-Language: en-US,en;q=0.8\r\n" +
                    "Cookie: _ga=GA1.3.93526326.1493301082; kabg=ya29.Gl05BH584vPBkzOKbAMGg1MST-BgKM7BVgBIKw31_vXojSA5gj2VEdk8BCCvcpnU1bINmJ0mH7X345jirhu6ILJ7EkKODexF4GwDfH9ZUpsoBKXA3aIIR7xhVhXugkY\r\n" +
                    "X-Request-Id: f2ba2c2f-0b4b-4d89-81ab-f4e88d2f83f5\r\n" +
                    "X-Forwarded-For: 213.115.136.18\r\n" +
                    "X-Forwarded-Proto: https\r\n" +
                    "X-Forwarded-Port: 443\r\n" +
                    "Via: 1.1 vegur\r\n" +
                    "Connect-Time: 0\r\n" +
                    "X-Request-Start: 1493301092766\r\n" +
                    "Total-Route-Time: 0\r\n" +
                    "Content-Length: 7\r\n" +
                    "\r\n" +
                    "String=";


    @Test
    public void getDomain_DomainMyDomain_DomainMyDomain() {
        UrlParserWithGSuiteAuth clone = UrlParserWithGSuiteAuth.builder()
                .authCookieName("MyCookie")
                .domain("MyDomain")
                .build();
        assertEquals("MyDomain", clone.getDomain());
    }


    @Test
    public void getAuthCookieName_CookieMyCookie_CookieMyCookie() {
        UrlParserWithGSuiteAuth clone = UrlParserWithGSuiteAuth.builder()
                .authCookieName("MyCookie")
                .domain("MyDomain")
                .build();
        assertEquals("MyCookie", clone.getAuthCookieName());
    }


    @Test
    public void getCloneGetDomain_DomainMyDomain_DomainMyDomain() {
        UrlParserWithGSuiteAuth clone = (UrlParserWithGSuiteAuth) UrlParserWithGSuiteAuth.builder()
                .authCookieName("MyCookie")
                .domain("MyDomain")
                .build()
                .getClone();
        assertEquals("MyDomain", clone.getDomain());
    }


    @Test
    public void getCloneGetAuthCookieName_CookieMyCookie_CookieMyCookie() {
        UrlParserWithGSuiteAuth clone = (UrlParserWithGSuiteAuth) UrlParserWithGSuiteAuth.builder()
                .authCookieName("MyCookie")
                .domain("MyDomain")
                .build()
                .getClone();
        assertEquals("MyCookie", clone.getAuthCookieName());
    }


    @Test
    public void getState_DomainMyDomain_DomainMyDomain() {
        String stateDomainActual = UrlParserWithGSuiteAuth.builder()
                .authCookieName("MyCookie")
                .domain("MyDomain")
                .build()
                .getState()
                .getJson()
                .getString("AuthDomain");
        assertEquals("MyDomain", stateDomainActual);
    }


    @Test
    public void getRequestGetMethodName_HttpHeaderAndGSuiteAuthMocked_encryptSSN() {
        UrlParserWithGSuiteAuth urlParser = UrlParserWithGSuiteAuth.builder()
                .authCookieName("kabg")
                .domain("kollektiva.se")
                .build();
        GSuiteAuth mockGSuiteAuth = Mockito.mock(GSuiteAuth.class);
        String authCookieValue = "ya29.Gl05BH584vPBkzOKbAMGg1MST-BgKM7BVgBIKw31_vXojSA5gj2VEdk8BCCvcpnU1bINmJ0mH7X345jirhu6ILJ7EkKODexF4GwDfH9ZUpsoBKXA3aIIR7xhVhXugkY";
        Mockito.when(mockGSuiteAuth.isUserLoggedIn(authCookieValue, "kollektiva.se")).thenReturn(true);
        urlParser.setGSuiteAuth(mockGSuiteAuth);
        Request request = urlParser.getRequest(HTTP_HEADER_WITH_AUTH_COOKIES);
        assertEquals("encryptSSN", request.getMethodName());
    }


    @Test
    public void getRequestGetMethodName_HttpHeaderAndGSuiteAuthMocked_ThrowsException() {
        UrlParserWithGSuiteAuth urlParser = UrlParserWithGSuiteAuth.builder()
                .authCookieName("kabg")
                .domain("kollektiva.se")
                .build();
        GSuiteAuth mockGSuiteAuth = Mockito.mock(GSuiteAuth.class);
        String authCookieValue = "dfsf";
        Mockito.when(mockGSuiteAuth.isUserLoggedIn(authCookieValue, "kollektiva.se")).thenReturn(true);
        urlParser.setGSuiteAuth(mockGSuiteAuth);
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> urlParser.getRequest(HTTP_HEADER_WITH_AUTH_COOKIES))
                .withMessageStartingWith("User was not logged in to the GSuite domain");
    }

}