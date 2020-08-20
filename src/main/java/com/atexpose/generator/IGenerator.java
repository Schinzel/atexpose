package com.atexpose.generator;

import com.atexpose.api.MethodObject;

import java.util.List;

public interface IGenerator {

    void generate(List<MethodObject> methods, List<Class> dataTypeList);
}
