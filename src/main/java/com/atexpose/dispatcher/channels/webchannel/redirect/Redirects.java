package com.atexpose.dispatcher.channels.webchannel.redirect;

import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import io.schinzel.basicutils.EmptyObjects;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

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


    /**
     * @param httpRequest
     * @return Empty string if argument request did not have a redirect. Else the url to redirect
     * to.
     */
    String getRedirect(HttpRequest httpRequest) {
        /*mRedirects.stream()
                .filter(r -> r.shouldRedirect(httpRequest))
                .findAny()
                .get()*/


        for (IRedirect redirect : mRedirects) {
            if (redirect.shouldRedirect(httpRequest)) {
                return redirect.getRedirect(httpRequest);
            }
        }
        return EmptyObjects.EMPTY_STRING;
    }


}
