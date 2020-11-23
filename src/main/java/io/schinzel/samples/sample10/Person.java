package io.schinzel.samples.sample10;

import io.schinzel.jstranspiler.transpiler.JsTranspiler_CreateSetter;

public class Person {
    public String name;
    @JsTranspiler_CreateSetter
    public int age;

    Person() {
    }

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String toString(){
        return "Manager with name " + name + " who is " + age + " years old";
    }
}

