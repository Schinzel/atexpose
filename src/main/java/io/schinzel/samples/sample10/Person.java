package io.schinzel.samples.sample10;

import com.atexpose.util.DateTimeStrings;
import io.schinzel.jstranspiler.transpiler.JsTranspiler_CreateSetter;

import java.time.Instant;

public class Person {
    public String name;
    @JsTranspiler_CreateSetter
    public int age;
    @JsTranspiler_CreateSetter
    public Instant timeStamp = Instant.now();

    Person() {
    }

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String toString(){
        return "Manager with name " + name
                + " who is " + age + " years old"
                + " with time stamp set to " + DateTimeStrings.getDateTimeUTC(timeStamp) + " UTC";
    }
}

