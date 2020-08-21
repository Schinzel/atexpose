package com.atexpose.api.datatypes;

import com.atexpose.Serialization;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ClassDT<T> extends AbstractDataType {
    @Getter
    private final Class<T> mClazz;

    public ClassDT(Class<T> clazz) {
        super(clazz.getSimpleName(), "The chars allowed for JSON strings");
        mClazz = clazz;
    }


    @Override
    protected boolean verifyValue(String value) {
        //A full eval for JSON is to expensive,
        //so only check is starts and ends with curly brackets
        return (value.startsWith("{") && value.endsWith("}"));
    }


    @Override
    protected T castToDataType(String value) {
        return Serialization.jsonStringToObject(value, mClazz);
    }

    @Override
    public String convertFromDataTypeToString(Object value) {
        return Serialization.objectToJsonString(value);
    }

}
