package io.schinzel.samples;

import com.atexpose.AtExpose;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * <p>
 * The purpose of this class is to show the a basic set up.
 * </p>
 * <p>
 * This sample exposes a class and an object.
 * A web server is started.
 * </p>
 * <p>
 * Test the below commands in a browser:
 * </p>
 * <pre>
 * http://127.0.0.1:5555/api/sayIt
 * http://127.0.0.1:5555/api/doubleIt?Int=55
 * http://127.0.0.1:5555/api/doEcho?String=chimp
 * http://127.0.0.1:5555/api/setTheThing?String=gorilla
 * http://127.0.0.1:5555/api/getTheThing
 * </pre>
 *
 * @author schinzel
 */
public class Sample2_WebServer {


    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject())
                //Start a web server
                .startWebServer();
    }
}
