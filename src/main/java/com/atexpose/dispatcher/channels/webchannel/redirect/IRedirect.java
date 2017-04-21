package com.atexpose.dispatcher.channels.webchannel.redirect;

import java.net.URI;

/**
 * The purpose of this interface is to
 * 1) answer the question if an http request should be redirected
 * 2) the redirect destination given an argument http request
 * <p>
 * Created by schinzel on 2017-04-20.
 */
public interface IRedirect {

    /**
     * @param uri An URI to check if it is to be redirected.
     * @return True if the argument URI should be redirected, else false.
     */
    boolean shouldRedirect(URI uri);


    /**
     * @param uri The URI from with the location to redirect from.
     * @return A new URI instance with the location to redirect to.
     */
    URI getRedirect(URI uri);
}
