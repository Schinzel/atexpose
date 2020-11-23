package io.schinzel.samples.sample10;

import com.atexpose.Expose;
import com.atexpose.Serialization;

public class SampleAPI {
    private Person manager = new Person("Henrik", 35);


    @Expose
    public Person getManager() {
        return manager;
    }


    @Expose(
            arguments = {"manager"},
            requiredArgumentCount = 1
    )
    public String setManager(Person manager) {
        System.out.println("Manager received server side");
        System.out.println(manager.toString());
        this.manager = manager;
        return "Manager set!";
    }
}
