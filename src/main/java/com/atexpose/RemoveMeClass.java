package com.atexpose;

@SuppressWarnings("unused")
public class RemoveMeClass {

    @Expose(
            arguments = {"String", "Int"}
    )
    public static String concat(String str1, Integer integer) {
        return str1 + integer.toString();
    }

    @Expose(
            arguments = {"test_var"}
    )
    public static RemoveMeVar test_it(RemoveMeVar test_var) {
        return new RemoveMeVar(test_var.s, test_var.i + 10);
    }

}
