package com.atexpose.dispatcher.channels.webchannel.redirect;

import io.schinzel.basicutils.thrower.Thrower;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * An instance of this class is a redirect from an host to another.
 * <p>
 * Created by schinzel on 2017-04-20.
 */
class HostRedirect implements IRedirect {
    private final String from;
    private final String to;


    HostRedirect(String from, String to) {
        Thrower.throwIfVarEmpty(from, "from");
        Thrower.throwIfVarEmpty(to, "to");
        this.from = from;
        this.to = to;
    }


    @Override
    public boolean shouldRedirect(URI uri) {
        return uri.getHost() != null && (uri.getHost().equalsIgnoreCase(this.from));
    }


    @Override
    @SneakyThrows
    public URI getNewLocation(URI uri) {
        return new URIBuilder(uri).setHost(this.to).build();
    }
}
