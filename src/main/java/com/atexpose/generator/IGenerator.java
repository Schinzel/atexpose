package com.atexpose.generator;

import com.atexpose.api.MethodObject;

import java.util.List;

/**
 * The purpose of a generator is to generate something from the API. For example documentation
 * or a Java or a JavaScript client.
 */
public interface IGenerator {

    /**
     * @param methods               All the methods in the API
     * @param customDataTypeClasses All added custom data types classes added to the API
     */
    void generate(List<MethodObject> methods, List<Class<?>> customDataTypeClasses);
}
