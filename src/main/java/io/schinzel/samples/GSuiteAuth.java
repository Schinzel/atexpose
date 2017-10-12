package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcherfactories.DispatcherFactory;
import com.atexpose.dispatcherfactories.WebServerBuilder;

/**
 * This sample sets a web server that requires you to be logged in to the argument domain
 * using Google.
 */
class GSuiteAuth {

    public static void main(String[] args) {
        AtExpose.create()
                //Start a web server without authentication
                .startDispatcher(getWebServerNoAuth())
                //Start a web server that requires authentication
                .startDispatcher(getWebServerWithAuth())
                //Start command line interface
                .startDispatcher(DispatcherFactory.cliBuilder().build());
    }


    private static Dispatcher getWebServerNoAuth() {
        return new WebServerBuilder().build();
    }


    private static Dispatcher getWebServerWithAuth() {
        return new WebServerBuilder()
                .port(5556)
                .webServerDir("web/auth_sample")
                .gSuiteAuth("bapp", "example.com")
                .build();
    }

}
