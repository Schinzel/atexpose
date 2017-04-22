package com.atexpose.dispatcher.channels.webchannel.redirect;

import io.schinzel.basicutils.Thrower;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

/**
 * An instance of this class is a redirect from a file to another.
 * <p>
 * Created by schinzel on 2017-04-20.
 */
public class FileRedirect implements IRedirect {
    private final String from;
    private final String to;


    /**
     * Examples of from and to arguments:
     * "index.html"
     * "/index.html"
     * "/a/b/c/summary.html"
     * "a/b/c/summary.html"
     *
     * @param from The file to redirect from.
     * @param to   The file to redirect to.
     * @return A new instance.
     */
    public static FileRedirect create(String from, String to) {
        return new FileRedirect(from, to);
    }


    private FileRedirect(String from, String to) {
        Thrower.throwIfEmpty(from, "from");
        Thrower.throwIfEmpty(to, "to");
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
