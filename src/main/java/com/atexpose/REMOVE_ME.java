package com.atexpose;

import com.atexpose.api.Argument;
import com.atexpose.api.datatypes.ClassDT;
import com.atexpose.generator.JsClientGenerator;

public class REMOVE_ME {
    public static void main(String[] args) {
        System.out.println("Here");
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                .addArgument(Argument.builder()
                        .name("test_var")
                        .dataType(new ClassDT(RemoveMeClass2.class))
                        .description("Reads incoming requests and sends the responses")
                        .build());



        atExpose
                .expose(RemoveMeClass.class)
                .startWebServer("remove_me")
                .startCLI()
                .generate(new JsClientGenerator("src/main/resources/remove_me/ServerCaller.js"));

    }
}
