package com.atexpose;

public class RemoveMeClass {

    @Expose(
            arguments = {"String", "Int"}
    )
    public static String concat(String str1, Integer integer) {
        return str1 + integer.toString();
    }

    @Expose
    public static Object test_it() {
        return new RemoveMeVar("dd", 77);
    }

    @Expose(
            arguments = {"test_var"}
    )
    public static String test_it_2(RemoveMeVar test_var){
        return "Dit it!";
    }

    @Expose
    public static RemoveMeVar test_it_3(){
        return new RemoveMeVar("bear", 33);
    }

}
