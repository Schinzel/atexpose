package com.atexpose.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;


/**
 * The purpose of this exception is to be thrown when an exposed class or object is invoked and
 * an exception occurs.
 * <p>
 * Created by schinzel on 2017-07-09.
 */
@AllArgsConstructor
public class ExposedInvocationException extends Exception {
    /** A set of properties for the error. E.g. name of class where exception occurred. */
    @Getter private final Map<String, String> properties;


}
