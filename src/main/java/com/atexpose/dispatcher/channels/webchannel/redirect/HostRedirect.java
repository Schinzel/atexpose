package com.atexpose.dispatcher.channels.webchannel.redirect;

import io.schinzel.basicutils.Thrower;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * An instance of this class is a redirect from an host to another.
 * <p>
 * Created by schinzel on 2017-04-20.
 */
public class HostRedirect implements IRedirect {
    private final String from;
    private final String to;


    public static HostRedirect create(String from, String to) {
        return new HostRedirect(from, to);
    }


    private HostRedirect(String from, String to) {
        Thrower.throwIfEmpty(from, "from");
        Thrower.throwIfEmpty(to, "to");
        this.from = from;
        this.to = to;
    }


    @Override
    public boolean shouldRedirect(URI uri) {
        return (uri.getHost().equalsIgnoreCase(this.from));
    }


    @Override
    @SneakyThrows
    public URI getRedirect(URI uri) {
        return new URIBuilder(uri).setHost(this.to).build();
    }
}
