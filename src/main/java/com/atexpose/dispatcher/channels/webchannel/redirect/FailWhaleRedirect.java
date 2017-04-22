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
public class FailWhaleRedirect implements IRedirect {
    private final String to;


    /**
     * Examples to argument:
     * "index.html"
     * "/index.html"
     * "/a/b/c/summary.html"
     * "a/b/c/summary.html"
     *
     * @param to The file to redirect to.
     * @return A new instance.
     */
    public static FailWhaleRedirect create(String to) {
        return new FailWhaleRedirect(to);
    }


    private FailWhaleRedirect(String to) {
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
