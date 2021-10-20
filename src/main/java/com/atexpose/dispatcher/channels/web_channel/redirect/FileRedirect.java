package com.atexpose.dispatcher.channels.web_channel.redirect;

import io.schinzel.basicutils.thrower.Thrower;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * An instance of this class is a redirect from a file to another.
 * <p>
 * Created by schinzel on 2017-04-20.
 */
class FileRedirect implements IRedirect {
    private final String from;
    private final String to;


    FileRedirect(String from, String to) {
        Thrower.throwIfVarEmpty(from, "from");
        Thrower.throwIfVarEmpty(to, "to");
        //Set from. Add "/" as first char if is missing
        this.from = (from.charAt(0) == '/') ? from : "/" + from;
        //Set to. Add "/" as first char if is missing
        this.to = (to.charAt(0) == '/') ? to : "/" + to;
    }


    @Override
    public boolean shouldRedirect(URI uri) {
        return (uri.getPath().equalsIgnoreCase(this.from));
    }


    @Override
    @SneakyThrows
    public URI getNewLocation(URI uri) {
        return new URIBuilder(uri).setPath(this.to).build();
    }
}
