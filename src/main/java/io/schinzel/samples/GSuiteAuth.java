package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.CliFactory;
import com.atexpose.dispatcher_factories.WebServerBuilder;

/**
 * This sample sets a web server that requires you to be logged in to the argument domain
 * using Google.
 */
class GSuiteAuth {

    public static void main(String[] args) {
        AtExpose.create()
                //Start a web server without authentication
                .start(getWebServerNoAuth())
                //Start a web server that requires authentication
                .start(getWebServerWithAuth())
                //Start command line interface
                .start(CliFactory.create());
    }


    private static IDispatcher getWebServerNoAuth() {
        return WebServerBuilder.create().build();
    }


    private static IDispatcher getWebServerWithAuth() {
        return WebServerBuilder.create()
                .port(5556)
                .webServerDir("web/auth_sample")
                .gSuiteAuth("bapp", "example.com")
                .build();
    }

}
