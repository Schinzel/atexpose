package com.atexpose.dispatcher;

/**
 * Holds properties for dispatcher.
 * <p>
 * Created by schinzel on 2017-04-23.
 */
public class PropertiesDispatcher {
    /** Name of the server in the http response header */
    public static final String RESP_HEADER_SERVER_NAME = "@Expose";
    /**
     * This marker is the suffix to all commands in http requests. E.g.
     * http://127.0.0.1:5555/call/setName?name=anyname If this suffix is not
     * present a file is assumed http;//127.0.0.1:5555/myfile.jpg
     */
    public static final String COMMAND_REQUEST_MARKER = "call/";


}
