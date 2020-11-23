package io.schinzel.samples;

import com.atexpose.AtExpose;
import io.schinzel.samples.auxiliary.MyClass;

/**
 * <p>
 * The purpose of this sample is to show how cookies are set and read.
 * </p>
 * <pre>
 * 1) Call below URL to set a cookie.
 * http://127.0.0.1:5555/api/setCookie
 * 2) Check in the browser that the cookie was set.
 * 3) Call below URL to read the cookie that was part of the request.
 * It's value will be written to standard out.
 * http://127.0.0.1:5555/api/readCookie
 * </pre>
 */
public class Sample9_Cookies {
    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Start a web server
                .startWebServer();
    }
}
