package com.atexpose;

import com.atexpose.dispatcherfactories.WebServerBuilder;
import com.atexpose.generator.JsClientGenerator;

public class REMOVE_ME {
    public static void main(String[] args) {
        System.out.println("Here");
        AtExpose.create()
                .startWebServer("remove_me")
                .generate(new JsClientGenerator("src/main/resources/remove_me/ServerCaller.js"));

    }
}
