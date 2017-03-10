package io.schinzel.samples;

import com.atexpose.AtExpose;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-03-03.
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
