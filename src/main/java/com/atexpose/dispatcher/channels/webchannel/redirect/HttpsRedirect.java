package com.atexpose.dispatcher.channels.webchannel.redirect;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * The purpose of this class is to redirect from http to https.
 * <p>
 * Created by Schinzel on 2017-04-21.
 */
public class HttpsRedirect implements IRedirect {

    public static HttpsRedirect create() {
        return new HttpsRedirect();
    }


    @Override
    public boolean shouldRedirect(URI uri) {
        return (uri.getScheme().equalsIgnoreCase("http"));
    }


    @Override
    @SneakyThrows
    public URI getRedirect(URI uri) {
        return new URIBuilder(uri).setScheme("https").build();
    }
}
