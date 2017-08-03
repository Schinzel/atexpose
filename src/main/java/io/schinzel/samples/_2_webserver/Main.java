package io.schinzel.samples._2_webserver;

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
        atExpose.getAPI()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject());
        atExpose
                //Start a command line interface
                .startCLI()
                //Start a web server
                .getWebServerBuilder().startWebServer();

    }
}
