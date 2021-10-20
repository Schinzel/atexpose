package com.atexpose.api;

import com.atexpose.api.data_types.AbstractDataType;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Accessors(prefix = "m")
public class Argument implements IValueWithKey, IStateNode {
    @Getter private final String mKey;
    @Getter private final String mDescription;
    //The data type of the argument
    @Getter private final AbstractDataType mDataType;
    // Holds the default value for this argument.
    @Getter private Object mDefaultValue = null;
    // Holds the string representation of the default value for this argument.
    @Getter private final String mDefaultValueAsString;
    @Getter private Pattern mAllowedCharsPattern = null;


    @Builder
    Argument(String name, String description, AbstractDataType dataType, String defaultValue, String allowedCharsRegEx) {
        this.mKey = name;
        this.mDescription = description;
        mDataType = dataType;
        if (defaultValue == null) {
            mDefaultValueAsString = "";
        } else {
            mDefaultValueAsString = defaultValue;
            mDefaultValue = mDataType.convertFromStringToDataType(defaultValue);
        }
        if (allowedCharsRegEx != null) {
            mAllowedCharsPattern = Pattern.compile(allowedCharsRegEx);
        }
    }


    /**
     * @param argumentValueAsString The string to check if it contains only valid chars
     * @return True if the argument string representation of an Argument value contains only valid chars.
     */
    public boolean containsAllowedChars(String argumentValueAsString) {
        return (mAllowedCharsPattern == null) || mAllowedCharsPattern.matcher(argumentValueAsString).matches();
    }


    @Override
    public State getState() {
        String allowedChars = (this.mAllowedCharsPattern == null)
                ? ""
                : this.mAllowedCharsPattern.toString();
        return State.getBuilder()
                .add("Name", this.getKey())
                .add("DataType", this.getDataType().getKey())
                .add("Description", this.getDescription())
                .add("DefaultValue", this.getDefaultValueAsString())
                .add("Pattern", allowedChars)
                .build();
    }
}
