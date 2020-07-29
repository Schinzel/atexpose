package com.atexpose;

public class RemoveMeClass {

    @Expose(
            arguments = {"String", "Int"}
    )
    public static String concat(String str1, Integer integer){
        return  str1 + integer.toString();
    }

}
