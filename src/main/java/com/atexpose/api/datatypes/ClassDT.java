package com.atexpose.api.datatypes;

import com.atexpose.Serialization;

public class ClassDT<T> extends AbstractDataType {
    private final Class mClazz;

    public ClassDT(Class clazz) {
        super(clazz.getSimpleName(), "Bla bla bla");
        mClazz = clazz;
    }


    @Override
    protected boolean verifyValue(String value) {
        return false;
    }


    @Override
    protected T castToDataType(String value) {
        return (T) Serialization.jsonStringToObject(value, mClazz);
    }
}
