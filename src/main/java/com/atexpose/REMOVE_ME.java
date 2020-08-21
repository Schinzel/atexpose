package com.atexpose;

import com.atexpose.api.Argument;
import com.atexpose.api.datatypes.ClassDT;
import com.atexpose.generator.JsClientGenerator;

public class REMOVE_ME {
    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                .addDataType(new ClassDT<>(RemoveMeVar.class))
                .addArgument(Argument.builder()
                        .name("test_var")
                        .dataType(new ClassDT<>(RemoveMeVar.class))
                        .description("Reads incoming requests and sends the responses")
                        .build());


        atExpose
                .expose(RemoveMeClass.class)
                //.startWebServer("remove_me")
                //.startCLI()
                .generate(new JsClientGenerator("src/main/resources/remove_me/ServerCaller.js"));

    }

}
