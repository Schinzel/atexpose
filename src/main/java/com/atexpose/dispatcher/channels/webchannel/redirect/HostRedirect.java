package com.atexpose.dispatcher.channels.webchannel.redirect;

import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * An instance of this class is a redirect from an host to another.
 * <p>
 * Created by schinzel on 2017-04-20.
 */
@Builder
public class HostRedirect implements IRedirect {
    private final String from;
    private final String to;


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
