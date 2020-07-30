package com.atexpose;

import com.atexpose.generator.JsClientGenerator;

public class REMOVE_ME {
    public static void main(String[] args) {
        System.out.println("Here");
        AtExpose.create()
                .expose(RemoveMeClass.class)
                .startWebServer("remove_me")
                .startCLI()
                .generate(new JsClientGenerator("src/main/resources/remove_me/ServerCaller.js"));

    }
}
