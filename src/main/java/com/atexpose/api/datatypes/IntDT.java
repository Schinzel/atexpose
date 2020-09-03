package com.atexpose.api.datatypes;


/**
 *
 * @author Schinzel
 */
public class IntDT extends AbstractDataType {
    IntDT() {
        super("Int",
                "Allowed values are whole numbers from -2147483647 to 2147483647. The non-numeric character "
                + "allowed is minus as the first character to indicate negative values.");
    }


    @Override
    public boolean verifyValue(String value) {
        if (value == null) {
            return false;
        }
        Integer myInt;
        try {
            myInt = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return (myInt != -2147483648);
    }


    @Override
    public Object castToDataType(String value) {
        return Integer.parseInt(value);
    }


}
