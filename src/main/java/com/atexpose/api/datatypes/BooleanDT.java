package com.atexpose.api.datatypes;

/**
 *
 * @author Schinzel
 */
public class BooleanDT extends AbstractDataType {

    BooleanDT() {
        super("boolean",
                "Allowed values are the strings 'true' or 'false'. The matching is case-insensitive.");
    }

    @Override
    public boolean verifyValue(String value) {
        return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
    }

    @Override
    public Object castToDataType(String value) {
        return value.equalsIgnoreCase("true");
    }


}
