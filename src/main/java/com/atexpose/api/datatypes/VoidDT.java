package com.atexpose.api.datatypes;

/**
 * Used to handle void returns from methods in Java and Unit returns from
 * functions in Kotlin. Used for returns only.
 */
public class VoidDT extends AbstractDataType {

    VoidDT() {
        super("void", "Void return. No return is allowed");
    }

    @Override
    public boolean verifyValue(String value) {
        return value == null;
    }

    @Override
    public Object castToDataType(String value) {
        throw new RuntimeException("Cannot cast string to data type " + VoidDT.class.getSimpleName() + ". This data type is for returns only.");
    }

    /**
     * @param value The value to convert to string
     * @return The argument value as string
     */
    @Override
    public String convertFromDataTypeToString(Object value) {
        return "";
    }

}
