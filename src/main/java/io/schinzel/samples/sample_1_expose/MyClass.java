package io.schinzel.samples.sample_1_expose;

import com.atexpose.Expose;

/**
 * The purpose of this class
 */
public class MyClass {

    @Expose
    public static String sayIt() {
        return "Helloooo world!";
    }


    @Expose(
            arguments = {"Int"},
            theReturn = "The argument number doubled"
    )
    public static int doubleIt(int i) {
        return i * 2;
    }


    @Expose(
            arguments = {"String"},
            theReturn = "The argument string prefixed with Echo"
    )
    public static String doEcho(String str) {
        return "Echo: " + str;
    }
}
