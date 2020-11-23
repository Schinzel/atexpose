package io.schinzel.samples.sample10;

import com.atexpose.Expose;

public class SampleAPI {
    private Person manager = new Person("Henrik", 35);
    private Position position = Position.FIRST;


    @Expose
    public Person getManager() {
        return manager;
    }


    @Expose(
            arguments = {"manager"},
            requiredArgumentCount = 1)
    public String setManager(Person manager) {
        System.out.println("Manager received server side: " + manager.toString());
        this.manager = manager;
        return "Manager set!";
    }


    @Expose
    public Position getPosition() {
        return position;
    }


    @Expose(
            arguments = {"position"},
            requiredArgumentCount = 1)
    public String setPosition(Position position) {
        System.out.println("Position received server side: " + position.name());
        this.position = position;
        return "Position set to " + position.name();
    }
}