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
        return new RemoveMeClass2("dd", 77);
    }

    @Expose(
            arguments = {"test_var"}
    )
    public static String test_it_2(RemoveMeClass2 test_var){
        return "Dit it!";
    }

    @Expose
    public static RemoveMeClass2 test_it_3(){
        return new RemoveMeClass2("bear", 33);
    }

}
