package com.atexpose.dispatcher_factories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.IChannel;
import com.atexpose.dispatcher.channels.web_channel.WebChannel;
import com.atexpose.dispatcher.channels.web_channel.redirect.Redirects;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.url_parser.UrlParser;
import com.atexpose.dispatcher.wrapper.IWrapper;
import com.atexpose.dispatcher.wrapper.web_wrapper.WebWrapper;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to offer a more readable
 * way to start a web server.
 *
 * @author schinzel
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
@Accessors(prefix = "m", fluent = true, chain = true)
public class WebServerBuilder {
    /** The port on which the web server will listen for incoming requests */
    @Setter int mPort = 5555;
    /** The number of thread the web server will have */
    @Setter int mNumberOfThreads = 10;
    /** The directory on drive in which the web server will look for static files to return */
    @Setter String mWebServerDir = "";
    /**
     * The access level this web server will have and as such which methods
     * this web server can call. Available values are 1-3.
     * 1 is the lowest access level. 3 is the highest.
     */
    @Setter int mAccessLevel = 1;
    /**
     * Sets the socket timeout. With this option set to a non-zero timeout,
     * a read() call on the InputStream associated with this Socket will block
     * for only this amount of time.
     */
    @Setter int mTimeoutInMillis = 300;
    /** The cache max-age sent to the browser. */
    @Setter int mBrowserCacheMaxAge = 1200;
    /** If true files read from drive are cached in RAM for increased performance */
    @Setter boolean mCacheFilesInRAM = true;
    Map<String, String> mServerSideVariables = new HashMap<>();
    /** Indicates if the default page should be forced an all requests */
    @Setter boolean mForceDefaultPage = false;
    private Map<String, String> mResponseHeaders = new HashMap<>();
    private Redirects.RedirectsBuilder mRedirectsBuilder = Redirects.getBuilder();
    /** File name custom 404 page */
    @Setter private String mFileName404Page;


    private WebServerBuilder() {
    }


    public static WebServerBuilder create() {
        return new WebServerBuilder();
    }


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
     * @param failWhalePage A path to an HTML page
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
     * @param failWhalePage A path to an HTML page
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


    private IWrapper getWrapper() {
        return WebWrapper.builder()
                .webServerDir(mWebServerDir)
                .browserCacheMaxAge(mBrowserCacheMaxAge)
                .cacheFilesInRam(mCacheFilesInRAM)
                .serverSideVariables(mServerSideVariables)
                .responseHeaders(mResponseHeaders)
                .fileName404Page(mFileName404Page)
                .build();
    }


    private IChannel getChannel() {
        return WebChannel.builder()
                .port(mPort)
                .timeout(mTimeoutInMillis)
                .redirects(mRedirectsBuilder.build())
                .build();
    }


    /**
     * Creates the web server.
     *
     * @return Status of the operation message.
     */
    public IDispatcher build() {
        //Construct web server name
        String webServerName = "WebServer_" + mPort;
        IChannel webChannel = this.getChannel();
        IParser parser = new UrlParser();
        IWrapper wrapper = this.getWrapper();
        return Dispatcher.builder()
                .name(webServerName)
                .channel(webChannel)
                .isSynchronized(false)
                .parser(parser)
                .wrapper(wrapper)
                .accessLevel(mAccessLevel)
                .noOfThreads(mNumberOfThreads)
                .build();
    }
}