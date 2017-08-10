package io.schinzel.samples;

import com.atexpose.AtExpose;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample exposes a class and an object.
 * A command line interface and a web server is started.
 * <p>
 * Test the below commands in a browser:
 * http://127.0.0.1:5555/call/sayIt
 * http://127.0.0.1:5555/call/doubleIt?Int=55
 * http://127.0.0.1:5555/call/doEcho?String=chimp
 * http://127.0.0.1:5555/call/setTheThing?String=gorilla
 * http://127.0.0.1:5555/call/getTheThing
 *
 * @author schinzel
 */
public class _Sample2_WebServer {


    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject());
        //Start a web server
        atExpose.getWebServerBuilder().startWebServer();


    }
}
