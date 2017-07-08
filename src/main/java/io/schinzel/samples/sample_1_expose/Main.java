package io.schinzel.samples.sample_1_expose;

import com.atexpose.AtExpose;

/**
 * This sample exposes a class and an object.
 * A command line interface is started.
 * <p>
 * In the terminal test:
 * sayIt
 * doubleIt 55
 * doEcho chimp
 * setIt gorilla
 * getIt
 * close
 *
 * @author schinzel
 */
public class Main {


    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject());
        //Start a command line interface
        atExpose.startCLI();

    }
}
