package com.atexpose.dispatcher.channels.webchannel.redirect;

import io.schinzel.basicutils.Thrower;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * The purpose of this class is to redirect to a fail whale page.
 * <p>
 * Created by schinzel on 2017-04-22.
 */
class FailWhaleRedirect implements IRedirect {
    private final String to;


    FailWhaleRedirect(String to) {
        Thrower.throwIfEmpty(to, "to");
        //Set to. Add "/" as first char if is missing
        this.to = (to.charAt(0) == '/') ? to : "/" + to;
    }


    @Override
    public boolean shouldRedirect(URI uri) {
        //Should redirect - i.e. return true - if the argument page is not the
        //same as the to page.
        return !(uri.getPath().equalsIgnoreCase(this.to));
    }


    @Override
    @SneakyThrows
    public URI getNewLocation(URI uri) {
        return new URIBuilder(uri).setPath(this.to).build();
    }
}
