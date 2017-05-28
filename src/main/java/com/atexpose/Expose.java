package com.atexpose;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Expose {
    String description() default "No description available";


    int requiredArgumentCount() default 0;


    int requiredAccessLevel() default 1;


    String[] arguments() default {};


    String[] labels() default {"Misc"};


    String theReturn() default "Result";


    String[] seeAlsos() default {};


    String[] aliases() default {};
}