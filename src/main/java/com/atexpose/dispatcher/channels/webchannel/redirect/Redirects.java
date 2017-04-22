package com.atexpose.dispatcher.channels.webchannel.redirect;

import lombok.experimental.Accessors;

import java.net.URI;
import java.util.List;

/**
 * The purpose of this class is to
 * 1) answer the question if an http request should be redirected
 * 2) the redirect destination given an argument http request
 * <p>
 * Created by schinzel on 2017-04-20.
 */
@Accessors(prefix = "m")
public class Redirects {
    private final List<IRedirect> mRedirects;


    @java.beans.ConstructorProperties({"redirects"})
    public Redirects(List<IRedirect> redirects) {
        this.mRedirects = redirects;
    }


    /**
     *
     * @param uri The URI to check if it should be redirected.
     * @return True if the argument URI should be redirected.
     */
    public boolean shouldRedirect(URI uri) {
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
                uri = redirect.getRedirect(uri);
            }
        }
        return uri;
    }


}
