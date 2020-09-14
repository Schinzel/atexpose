package com.atexpose.api.datatypes;

import com.atexpose.Serialization;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Accessors(prefix = "m")
public class ClassDT<T> extends AbstractDataType {
    @Getter
    private final Class<T> mClazz;
    // 1) Starts with "
    // 2) followed by any number of chars that are uppercase chars, any number or underscore or minus
    // 3) ending with a "
    private static final Pattern ENUM_VALUE_PATTERN = Pattern.compile("^[\"][A-Z0-9_-]*[\"]");

    public ClassDT(Class<T> clazz) {
        super(clazz.getSimpleName(), "The chars allowed for JSON strings");
        mClazz = clazz;
    }


    @Override
    protected boolean verifyValue(String value) {
        //A full eval for JSON is to expensive,
        //so only check is starts and ends with curly brackets
        return (value.startsWith("{") && value.endsWith("}"))
                // Or this is the value for an enum
                || ENUM_VALUE_PATTERN.matcher(value).matches();
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
