package io.schinzel.samples;

import com.atexpose.AtExpose;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample exposes a class and an object.
 * A command line interface and a web server is started
 * The web server serves files from the directory "web/sample3"
 * <p>
 * Test the below commands in a browser:
 * http://127.0.0.1:5555/mypage.html
 *
 * @author schinzel
 */
public class _Sample3_WebServer {


    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject());
        //Get web server builder
        atExpose.getWebServerBuilder()
                //Set the dir from which requested file will be read
                .webServerDir("web/sample3")
                //Disable RAM cache so that changes to files made kicks through without restarting the web server
                .cacheFilesInRAM(false)
                //Start a web server
                .startWebServer();

    }
}
