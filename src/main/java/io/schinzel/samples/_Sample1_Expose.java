package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcherfactories.DispatcherFactory;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample exposes a class and an object.
 * A command line interface is started.
 * <p>
 * In the terminal test:
 * sayIt
 * doubleIt 55
 * doEcho chimp
 * setTheThing gorilla
 * getTheThing
 * close
 *
 * @author schinzel
 */
public class _Sample1_Expose {


    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject())
                //Start a command line interface
                .startDispatcher(DispatcherFactory.cliBuilder().build());

    }
}
