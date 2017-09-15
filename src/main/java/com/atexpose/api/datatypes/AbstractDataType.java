package com.atexpose.api.datatypes;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Schinzel
 */
@AllArgsConstructor
public abstract class AbstractDataType implements IValueWithKey, IStateNode {
    @Getter @NonNull final String key;
    @Getter @NonNull final String allowedValueDesc;
    // ----------------------------------------------------------------
    // ABSTRACT METHODS
    // ----------------------------------------------------------------


    /**
     * Verify that the string representation of the data type is correct. For
     * example, the data type boolean might have "false" and "true" as the only
     * valid string representations.
     *
     * @param value The value to verify.
     * @return True if the argument value as a valid representation, else false.
     */
    protected abstract boolean verifyValue(String value);


    /**
     * Converts the argument value to the implementing data type. For example,
     * if the data type is a Float then convert the string "44.5" to the Float
     * 44.5.
     *
     * @param value The value to convert
     * @return The generated data type.
     */
    protected abstract Object castToDataType(String value);


    public boolean isJson() {
        return false;
    }


    /**
     * @param value The value to convert
     * @return The value cast to the data type. For example a Boolean
     */
    public Object convertFromStringToDataType(String value) {
        //If the argument string representation is not correct
        if (!this.verifyValue(value)) {
            throw new RuntimeError("Error when converting '" + value + "' to " + this.getKey() + ". " + this.getAllowedValueDesc());
        }
        //Convert the string to the data type and return it.
        return this.castToDataType(value);
    }


    /**
     * @param value The value to convert to string
     * @return The argument value as string
     */
    public Object convertFromDataTypeToString(Object value) {
        return value.toString();
    }
    // ----------------------------------------------------------------
    // STATUS 
    // ----------------------------------------------------------------


    public State getState() {
        return State.getBuilder()
                .add("Name", this.getKey())
                .add("AllowedValues", this.getAllowedValueDesc())
                .build();
    }

}
