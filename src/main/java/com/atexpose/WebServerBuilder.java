package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.AbstractChannel;
import com.atexpose.dispatcher.channels.webchannel.WebChannel;
import com.atexpose.dispatcher.channels.webchannel.redirect.FileRedirect;
import com.atexpose.dispatcher.channels.webchannel.redirect.HostRedirect;
import com.atexpose.dispatcher.channels.webchannel.redirect.HttpsRedirect;
import com.atexpose.dispatcher.channels.webchannel.redirect.IRedirect;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.urlparser.URLParser;
import com.atexpose.dispatcher.parser.urlparser.UrlParserWithGSuiteAuth;
import com.atexpose.dispatcher.wrapper.IWrapper;
import com.atexpose.dispatcher.wrapper.webresponse.WebWrapper;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.keyvalues.KeyValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    boolean mForceHttps = false;
    boolean mUseCachedFiles = true;
    Map<String, String> mServerSideVariables = new HashMap<>();
    String mDefaultPage = "index.html";
    boolean mForceDefaultPage = false;
    private Map<String, String> mResponseHeaders = new HashMap<>();
    private List<IRedirect> mRedirects = new ArrayList<>();
    private final API mAPI;
    private String mAuthCookieName;
    private String mAuthDomain;
    private final KeyValues<Dispatcher> mDispatchers;


    WebServerBuilder(API api, KeyValues<Dispatcher> dispatchers) {
        mAPI = api;
        mDispatchers = dispatchers;
    }


    /**
     * Add redirect from a file path to another
     * Example
     * .addFileRedirect("home.html", "index.html")
     *
     * @param source      file path to file we do not want to show
     * @param destination file path to file we want to redirect user to
     * @return This for chaining.
     */
    public WebServerBuilder addFileRedirect(String source, String destination) {
        mRedirects.add(FileRedirect.create(source, destination));
        return this;
    }


    /**
     * Add redirect from one host to anther.
     * Enter without protocol.
     * Example
     * .addHostRedirect("www.otherdomain.se", "www.thisdomain.se")
     * .addHostRedirect("otherdomain.se", "www.otherdomain.se")
     *
     * @param source      host address we want to redirect from
     * @param destination host address we want to redirect to
     * @return This for chaining.
     */
    public WebServerBuilder addHostRedirect(String source, String destination) {
        mRedirects.add(HostRedirect.create(source, destination));
        return this;
    }


    /**
     * If set to true the server redirects http calls to https.
     *
     * @param forceHttps If true, all http requests will be redirected to https.
     * @return This for chaining.
     */
    public WebServerBuilder forceHttps(boolean forceHttps) {
        mRedirects.add(HttpsRedirect.create());
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
     * Default page to return on default request
     *
     * @param htmlPage Name of the HTML-page.
     * @return This for chaining.
     */
    public WebServerBuilder defaultHtmlPage(String htmlPage) {
        mDefaultPage = htmlPage;
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
                .defaultPage(mDefaultPage)
                .forceDefaultPage(mForceDefaultPage)
                .serverSideVariables(mServerSideVariables)
                .responseHeaders(mResponseHeaders)
                .build();
    }


    /**
     * @return If there has been an auth domain set, a UrlParserWithGSuiteAuth is
     * returned. Else a URLParser is returned.
     */
    private AbstractParser getParser() {
        return (Checker.isEmpty(mAuthDomain)) ?
                new URLParser() :
                UrlParserWithGSuiteAuth.builder()
                        .authCookieName(mAuthCookieName)
                        .domain(mAuthDomain)
                        .build();
    }


    private AbstractChannel getChannel() {
        return WebChannel.builder()
                .port(mPort)
                .timeout(mTimeout)
                .redirectList(mRedirects)
                .build();
    }


    /**
     * Creates the web server.
     *
     * @return Status of the operation message.
     */
    public Dispatcher startWebServer() {
        //Construct web server name
        String webServerName = "WebServer_" + mPort;
        AbstractChannel webChannel = this.getChannel();
        AbstractParser parser = this.getParser();
        IWrapper wrapper = this.getWrapper();
        Dispatcher dispatcher = Dispatcher.builder()
                .name(webServerName)
                .channel(webChannel)
                .parser(parser)
                .wrapper(wrapper)
                .accessLevel(mAccessLevel)
                .noOfThreads(mNoOfThreads)
                .api(mAPI)
                .build();
        mDispatchers.add(dispatcher);
        dispatcher.commenceMessaging(false);
        return dispatcher;
    }

}
