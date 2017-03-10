package com.atexpose.api.datatypes;

/**
 *
 * @author Schinzel
 */
public class FloatDT extends AbstractDataType {

    FloatDT() {
        super("Float", "Allowed values are numeric chars. The non-numeric characters allowed are minus as the "
                + "first character to indicate negative values and dot as decimal delimiter.");
    }

    @Override
    public boolean verifyValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    @Override
    public Object castToDataType(String value) {
        return Float.parseFloat(value);
    }


}
