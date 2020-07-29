package com.atexpose.api.datatypes;

/**
 *
 * @author Schinzel
 */
public class StringDT extends AbstractDataType {
    static final int MAX_LENGTH = 500000;
    private static final String STRING_REPRESENTATION_OF_NULL = "null";


    StringDT() {
        super("string",
                "Allowed values are arbitrary characters. Reserved value is the null value '" + STRING_REPRESENTATION_OF_NULL + "'. "
                + "The maximum length is " + MAX_LENGTH + " characters.");
    }


    StringDT(String dataTypeName, String allowedValueMessage) {
        super(dataTypeName, allowedValueMessage);
    }


    @Override
    public boolean verifyValue(String value) {
        return value != null && !value.equalsIgnoreCase(STRING_REPRESENTATION_OF_NULL) && value.length() <= MAX_LENGTH;
    }


    @Override
    public Object castToDataType(String value) {
        return value;
    }

}
