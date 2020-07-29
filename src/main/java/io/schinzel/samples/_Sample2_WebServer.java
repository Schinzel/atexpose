package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcherfactories.WebServerBuilder;
import com.atexpose.generator.JsClientGenerator;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample exposes a class and an object.
 * A command line interface and a web server is started.
 * <p>
 * Test the below commands in a browser:
 * http://127.0.0.1:5555/api/sayIt
 * http://127.0.0.1:5555/api/doubleIt?Int=55
 * http://127.0.0.1:5555/api/doEcho?String=chimp
 * http://127.0.0.1:5555/api/setTheThing?String=gorilla
 * http://127.0.0.1:5555/api/getTheThing
 *
 * @author schinzel
 */
public class _Sample2_WebServer {


    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject())
                //Start a web server
                .start(WebServerBuilder.create().build())
                .generate(new JsClientGenerator("src/main/resources/ServerCaller.js"));
    }
}
