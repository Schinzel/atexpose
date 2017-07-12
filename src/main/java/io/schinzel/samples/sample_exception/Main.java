package io.schinzel.samples.sample_exception;

import com.atexpose.AtExpose;

/**
 * The purpose of this sample is to show how to attach customer key-value properties to an
 * exception.
 * <p>
 * 1) Start main method below
 * 2) Type myMethod in console.
 * 3) Call http://localhost:5555/call/myMethod with a browser.
 */
public class Main {
    public static void main(String[] args) {
        AtExpose.create()
                .expose(MyClass.class)
                .startCLI()
                .getWebServerBuilder()
                .startWebServer();
    }
}
