package io.schinzel.samples.sample_3_webserver;

import com.atexpose.AtExpose;

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
                .getWebServerBuilder()
                .webServerDir("web/sample3")
                //Disable RAM cache so that change made kicks through without restarting the web server
                .cacheFilesInRAM(false)
                .startWebServer();

    }
}
