package com.atexpose.dispatcher.channels.webchannel.redirect;

import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;

/**
 * The purpose of this interface is to
 * 1) answer the question if an http request should be redirected
 * 2) the redirect destination given an argument http request
 * <p>
 * Created by schinzel on 2017-04-20.
 */
public interface IRedirect {

    boolean shouldRedirect(HttpRequest httpRequest);


    String getRedirect(HttpRequest httpRequest);
}
