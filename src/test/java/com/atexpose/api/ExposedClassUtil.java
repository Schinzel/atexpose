package com.atexpose.api;

/**
 * The purpose of this class is to be used for test where methods are exposed.
 * <p>
 * Created by schinzel on 2017-06-04.
 */
public class ExposedClassUtil {

    public String doIt() {
        new MyInnerClass().doAnOperation();
        return "Did it!";
    }


    class MyInnerClass {

        public void doAnOperation() {
            throw new RuntimeException("Something went wrong!");
        }

    }
}
