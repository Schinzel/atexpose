package io.schinzel.samples;

import com.atexpose.AtExpose;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * <p>
 * The purpose of this class is to show the a basic set up.
 * </p>
 * <p>
 * This sample exposes a class and an object.
 * A command line interface is started.
 * </p>
 * <p>
 * In the terminal test:
 * </p>
 * <pre>
 * sayIt
 * doubleIt 55
 * doEcho chimp
 * setTheThing gorilla
 * getTheThing
 * shutdown
 * </pre>
 *
 * @author schinzel
 */
public class Sample1_Expose {


    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject())
                //Start a command line interface
                .startCLI();

    }
}
