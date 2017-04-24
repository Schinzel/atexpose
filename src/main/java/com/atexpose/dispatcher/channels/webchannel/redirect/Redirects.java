package com.atexpose.dispatcher.channels.webchannel.redirect;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.net.URI;

/**
 * The purpose of this class is to
 * 1) answer the question if an http request should be redirected
 * 2) the redirect destination given an argument http request
 * <p>
 * Created by schinzel on 2017-04-20.
 */
@Accessors(prefix = "m")
@AllArgsConstructor
public class Redirects {
    private final ImmutableList<IRedirect> mRedirects;


    public static RedirectsBuilder getBuilder() {
        return new RedirectsBuilder();
    }


    public static class RedirectsBuilder {
        FailWhaleRedirect mFailWhaleRedirect;
        ImmutableList.Builder<IRedirect> mRedirectListBuilder = new ImmutableList.Builder<>();


        /**
         * Redirects all request to a single page. Typically used to set up a fail whale.
         * <p>
         * Argument example:
         * "fail.html"
         * "/error.html"
         * "/a/b/c/info.html"
         * "a/b/c/index.html"
         *
         * @param failWhalePage The file to redirect to.
         * @return A new instance.
         */
        public RedirectsBuilder setFailWhaleRedirect(String failWhalePage) {
            mFailWhaleRedirect = new FailWhaleRedirect(failWhalePage);
            return this;
        }


        /**
         * All http requests will be redirected to https.
         *
         * @return This of chaining.
         */
        public RedirectsBuilder setHttpsRedirect() {
            mRedirectListBuilder.add(new HttpsRedirect());
            return this;
        }


        /**
         * Examples of from and to arguments:
         * "index.html"
         * "/index.html"
         * "/a/b/c/summary.html"
         * "a/b/c/summary.html"
         *
         * @param from The file to redirect from.
         * @param to   The file to redirect to.
         * @return A new instance.
         */
        public RedirectsBuilder addFileRedirect(String from, String to) {
            mRedirectListBuilder.add(new FileRedirect(from, to));
            return this;
        }


        /**
         * Examples of from and to arguments:
         * "example.com"
         * "www.example.com"
         * "sub1.example.com"
         * "sub2.example.com"
         * "www.schinzel.io"
         *
         * @param from The domain to redirect from.
         * @param to   The domain to redirect ot.
         * @return A new instance.
         */
        public RedirectsBuilder addHostRedirect(String from, String to) {
            mRedirectListBuilder.add(new HostRedirect(from, to));
            return this;
        }


        public Redirects build() {
            //If a fail whale page has been set
            if (mFailWhaleRedirect != null) {
                //Add it to list
                mRedirectListBuilder.add(mFailWhaleRedirect);
            }
            return new Redirects(mRedirectListBuilder.build());

        }

    }


    /**
     * @param uri The URI to check if it should be redirected.
     * @return True if the argument URI should be redirected.
     */
    public boolean shouldRedirect(URI uri) {
        if (Redirects.isMethodCall(uri)){
            return false;
        }
        for (IRedirect redirect : mRedirects) {
            if (redirect.shouldRedirect(uri)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param uri An URI to redirect.
     * @return Zero, one or several redirects applied to the argument URI.
     */
    public URI getNewLocation(URI uri) {
        for (IRedirect redirect : mRedirects) {
            if (redirect.shouldRedirect(uri)) {
                uri = redirect.getNewLocation(uri);
            }
        }
        return uri;
    }


    /**
     *
     * @param uri
     * @return True if the argument URI is a method call, else false.
     */
    static boolean isMethodCall(URI uri){
        return uri.getPath().startsWith('/' + PropertiesDispatcher.COMMAND_REQUEST_MARKER);
    }


}
