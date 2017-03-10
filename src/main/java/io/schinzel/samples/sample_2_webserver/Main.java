package io.schinzel.samples.sample_2_webserver;

import com.atexpose.AtExpose;

/**
 * This sample exposes a class and an object.
 * A command line interface and a web server is started.
 * <p>
 * Test the below commands in a browser:
 * http://127.0.0.1:5555/call/sayIt
 * http://127.0.0.1:5555/call/doubleIt?Int=55
 * http://127.0.0.1:5555/call/doEcho?String=chimp
 * http://127.0.0.1:5555/call/setIt?String=gorilla
 * http://127.0.0.1:5555/call/getIt
 *
 * @author schinzel
 */
public class Main {


    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        //Expose static methods in a class
        atExpose.getAPI().expose(MyClass.class);
        //Expose an instance
        atExpose.getAPI().expose(new MyObject());
        //Start a command line interface
        atExpose.startCLI();
        //Start a web server
        atExpose.getWebServerBuilder().startWebServer();

    }
}
