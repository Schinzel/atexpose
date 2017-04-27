package com.atexpose.dispatcher.parser.urlparser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlParserWithGSuiteAuthTest {


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


}