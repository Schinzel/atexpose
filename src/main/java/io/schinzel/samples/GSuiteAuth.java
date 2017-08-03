package io.schinzel.samples;

import com.atexpose.AtExpose;

/**
 * This sample sets a web server that requires you to be logged in to the argument domain
 * using Google.
 */
class GSuiteAuth {

    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        //Start a web server without authentication
        atExpose.getWebServerBuilder()
                .startWebServer();
        //Start a web server that requires authentication
        atExpose.getWebServerBuilder()
                .port(5556)
                .webServerDir("web/auth_sample")
                .gSuiteAuth("bapp", "example.com")
                .startWebServer();
        //Start command line interface
        atExpose.startCLI();
    }

}
