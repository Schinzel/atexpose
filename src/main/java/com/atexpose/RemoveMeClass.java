package com.atexpose;

@SuppressWarnings("unused")
public class RemoveMeClass {

    @Expose(
            arguments = {"String", "Int"},
            requiredArgumentCount = 2
    )
    public static String concat(String str1, Integer integer) {
        return str1 + integer.toString();
    }

    @Expose(
            arguments = {"test_var"},
            requiredArgumentCount = 1
    )
    public static RemoveMeVar test_it(RemoveMeVar test_var) {
        return new RemoveMeVar(test_var.s, test_var.i + 10);
    }


    @Expose(
            arguments = {"test_enum"}
    )
    public static RemoveMeEnum test_it_2(RemoveMeEnum test_enum) {
        return RemoveMeEnum.SECOND;
    }

}
