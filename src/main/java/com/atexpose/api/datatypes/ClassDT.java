package com.atexpose.api.datatypes;

import com.atexpose.Serialization;

public class ClassDT extends AbstractDataType {
    private final Class mClazz;

    public ClassDT(Class clazz) {
        super(clazz.getSimpleName(), "Bla bla bla");
        mClazz = clazz;
    }


    @Override
    protected boolean verifyValue(String value) {
        return true;
    }


    @Override
    protected Object castToDataType(String value) {
        return Serialization.jsonStringToObject(value, mClazz);
    }
}
