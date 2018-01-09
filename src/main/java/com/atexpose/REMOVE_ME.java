package com.atexpose;

import com.atexpose.dispatcherfactories.WebServerBuilder;

public class REMOVE_ME {
    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(new MyClass())
                //Start web server
                .start(WebServerBuilder.create().build());
    }
}
