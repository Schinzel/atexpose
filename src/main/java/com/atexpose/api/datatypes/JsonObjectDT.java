package com.atexpose.api.datatypes;

import org.json.JSONObject;

/**
 *
 * @author schinzel
 */
public class JsonObjectDT extends AbstractDataType {
    JsonObjectDT() {
        super("JSONObject",
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
        return new JSONObject(value);
    }

}
