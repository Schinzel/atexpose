package io.schinzel.samples.exception;

import com.atexpose.AtExpose;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.dispatcherfactories.WebServerBuilder;
import io.schinzel.samples.auxiliary.MyClass;

/**
 * The purpose of this sample is to show how to attach custom key-value properties to an
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
                .start(CliFactory.create())
                .start(WebServerBuilder.create().build());
    }
}
