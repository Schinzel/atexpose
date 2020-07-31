package com.atexpose.api.datatypes;

import com.atexpose.Serialization;

public class ObjectDT extends AbstractDataType {
    ObjectDT() {
        super("object",
                "See json.org");
    }


    @Override
    public boolean isJson() {
        return true;
    }


    @Override
    public boolean verifyValue(String value) {
        //A full eval for JSON is to expensive,
        //so only check is starts and ends with curly brackets
        return (value.startsWith("{") && value.endsWith("}"));
    }


    @Override
    public Object castToDataType(String value) {
        throw new RuntimeException("Not yet supported");
    }


    public String convertFromDataTypeToString(Object value) {
        return Serialization.objectToJsonString(value);
    }

}
