package com.atexpose.dispatcher.channels.webchannel.redirect;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
@AllArgsConstructor
@Accessors(prefix = "m")
public class Redirects {
    private final List<IRedirect> mRedirects;


    public boolean shouldRedirect(URI uri) {
        for (IRedirect redirect : mRedirects) {
            if (redirect.shouldRedirect(uri)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param uri
     * @return
     */
    @SneakyThrows
    URI getRedirects(URI uri) {
        for (IRedirect redirect : mRedirects) {
            if (redirect.shouldRedirect(uri)) {
                uri = redirect.getRedirect(uri);
            }
        }
        return uri;
    }


}
