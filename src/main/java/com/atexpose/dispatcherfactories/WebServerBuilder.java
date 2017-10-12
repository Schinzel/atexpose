package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.dispatcher.channels.webchannel.WebChannel;
import com.atexpose.dispatcher.channels.webchannel.redirect.Redirects;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.urlparser.UrlParser;
import com.atexpose.dispatcher.parser.urlparser.UrlParserWithGSuiteAuth;
import com.atexpose.dispatcher.wrapper.IWrapper;
import com.atexpose.dispatcher.wrapper.WebWrapper;
import io.schinzel.basicutils.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to offer a more readable
 * way to start a web server.
 *
 * @author schinzel
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class WebServerBuilder {
    int mPort = 5555;
    int mNoOfThreads = 10;
    String mWebServerDir = "web";
    int mAccessLevel = 1;
    int mTimeout = 300;
    int mBrowserCacheMaxAge = 1200;
    boolean mUseCachedFiles = true;
    Map<String, String> mServerSideVariables = new HashMap<>();
    boolean mForceDefaultPage = false;
    private Map<String, String> mResponseHeaders = new HashMap<>();
    private Redirects.RedirectsBuilder mRedirectsBuilder = Redirects.getBuilder();
    private String mAuthCookieName;
    private String mAuthDomain;


    /**
     * Add redirect from a file path to another
     * Example
     * .addFileRedirect("home.html", "index.html")
     *
     * @param from file path to file we do not want to show
     * @param to   file path to file we want to redirect user to
     * @return This for chaining.
     */
    public WebServerBuilder addFileRedirect(String from, String to) {
        mRedirectsBuilder.addFileRedirect(from, to);
        return this;
    }


    /**
     * Add redirect from one host to another.
     * Example
     * .addHostRedirect("www.otherdomain.se", "www.thisdomain.se")
     * .addHostRedirect("otherdomain.se", "www.otherdomain.se")
     *
     * @param from host address we want to redirect from
     * @param to   host address we want to redirect to
     * @return This for chaining.
     */
    public WebServerBuilder addHostRedirect(String from, String to) {
        mRedirectsBuilder.addHostRedirect(from, to);
        return this;
    }


    /**
     * If set to true the server redirects http requests to https.
     *
     * @param forceHttps If true, all http requests will be redirected to https. If false, this
     *                   method will do nothing.
     * @return This for chaining.
     */
    public WebServerBuilder forceHttps(boolean forceHttps) {
        if (forceHttps) {
            mRedirectsBuilder.setHttpsRedirect();
        }
        return this;
    }


    /**
     * Redirects all request to a single page. Typically used to set up a fail whale.
     *
     * @param failWhalePage
     * @return This for chaining.
     */
    public WebServerBuilder setFailWhaleRedirect(String failWhalePage) {
        mRedirectsBuilder.setFailWhaleRedirect(failWhalePage);
        return this;
    }


    /**
     * Redirects all request to a single page. Typically used to set up a fail whale.
     * <p>
     * Argument examples:
     * "fail.html"
     * "/error.html"
     * "/a/b/c/info.html"
     * "a/b/c/index.html"
     *
     * @param failWhalePage
     * @param useFailWhalePage If true the argument fail whale page will be redirect to. If false,
     *                         this method will do nothing.
     * @return This for chaining.
     */
    public WebServerBuilder setFailWhaleRedirect(String failWhalePage, boolean useFailWhalePage) {
        if (useFailWhalePage) {
            mRedirectsBuilder.setFailWhaleRedirect(failWhalePage);
        }
        return this;
    }


    public WebServerBuilder addServerSideVar(String name, String value) {
        mServerSideVariables.put(name, value);
        return this;
    }


    public WebServerBuilder addResponseHeader(String key, String value) {
        mResponseHeaders.put(key, value);
        return this;
    }


    public WebServerBuilder gSuiteAuth(String authCookieName, String domain) {
        mAuthCookieName = authCookieName;
        mAuthDomain = domain;
        return this;
    }


    /**
     * The port on which the web server will listen for incoming requests.
     *
     * @param Port The port. Typical values are 8080, 5555.
     * @return This for chaining.
     */
    public WebServerBuilder port(int Port) {
        this.mPort = Port;
        return this;
    }


    /**
     * @param numberOfThreads The number of thread the web server will have.
     * @return This for chaining.
     */
    public WebServerBuilder numberOfThreads(int numberOfThreads) {
        this.mNoOfThreads = numberOfThreads;
        return this;
    }


    /**
     * The directory on drive in which the web server will look for static files
     * to return.
     *
     * @param WebServerDir The name of the directory. Sample value "web/mysite".
     * @return This for chaining.
     */
    public WebServerBuilder webServerDir(String WebServerDir) {
        this.mWebServerDir = WebServerDir;
        return this;
    }


    /**
     * The access level this web server will have and as such which methods
     * this web server can call. Available values are 1-3.
     * 1 is the lowest access level. 3 is the highest.
     *
     * @param AccessLevel An access level 1-3.
     * @return This for chaining.
     */
    public WebServerBuilder accessLevel(int AccessLevel) {
        this.mAccessLevel = AccessLevel;
        return this;
    }


    /**
     * Sets the socket timeout. With this option set to a non-zero timeout,
     * a read() call on the InputStream associated with this Socket will block
     * for only this amount of time.
     *
     * @param Timeout Timeout in milliseconds
     * @return This for chaining.
     */
    public WebServerBuilder timeoutInMillis(int Timeout) {
        this.mTimeout = Timeout;
        return this;
    }


    /**
     * The cache max-age sent to the browser.
     *
     * @param cacheMaxAgeSeconds The browser cache instruction in seconds.
     * @return This for chaining.
     */
    public WebServerBuilder browserCacheMaxAge(int cacheMaxAgeSeconds) {
        this.mBrowserCacheMaxAge = cacheMaxAgeSeconds;
        return this;
    }


    /**
     * If true files read from drive are caches in RAM.
     *
     * @param useCachedFiles If true static web content such as HTML files
     *                       will be cached in RAM for better performance.
     * @return This for chaining.
     */
    public WebServerBuilder cacheFilesInRAM(boolean useCachedFiles) {
        this.mUseCachedFiles = useCachedFiles;
        return this;
    }


    /**
     * Indicates if the default page should be forced an all requests
     *
     * @param forceDefaultPage If true, default is returned for a requests.
     * @return This for chaining.
     */
    public WebServerBuilder forceDefaultPage(boolean forceDefaultPage) {
        mForceDefaultPage = forceDefaultPage;
        return this;
    }


    private IWrapper getWrapper() {
        return WebWrapper.builder()
                .webServerDir(mWebServerDir)
                .browserCacheMaxAge(mBrowserCacheMaxAge)
                .cacheFilesInRam(mUseCachedFiles)
                .serverSideVariables(mServerSideVariables)
                .responseHeaders(mResponseHeaders)
                .build();
    }


    /**
     * @return If there has been an auth domain set, a UrlParserWithGSuiteAuth is
     * returned. Else a URLParser is returned.
     */
    private IParser getParser() {
        return (Checker.isEmpty(mAuthDomain)) ?
                new UrlParser() :
                UrlParserWithGSuiteAuth.builder()
                        .authCookieName(mAuthCookieName)
                        .domain(mAuthDomain)
                        .build();
    }


    private IChannel getChannel() {
        return WebChannel.builder()
                .port(mPort)
                .timeout(mTimeout)
                .redirects(mRedirectsBuilder.build())
                .build();
    }


    /**
     * Creates the web server.
     *
     * @return Status of the operation message.
     */
    public Dispatcher build() {
        //Construct web server name
        String webServerName = "WebServer_" + mPort;
        IChannel webChannel = this.getChannel();
        IParser parser = this.getParser();
        IWrapper wrapper = this.getWrapper();
        return Dispatcher.builder()
                .name(webServerName)
                .channel(webChannel)
                .isSynchronized(false)
                .parser(parser)
                .wrapper(wrapper)
                .accessLevel(mAccessLevel)
                .noOfThreads(mNoOfThreads)
                .build();
    }

}
