package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.api.datatypes.DataTypeEnum;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

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


    @Builder
    Argument(String name, String description, DataTypeEnum dataType, String defaultValue) {
        this.mKey = name;
        this.mDescription = description;
        mDataType = dataType.getInstance();
        if (defaultValue == null) {
            mDefaultValueAsString = "";
        } else {
            mDefaultValueAsString = defaultValue;
            mDefaultValue = mDataType.convertFromStringToDataType(defaultValue);
        }
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", this.getKey())
                .add("DataType", this.getDataType().getKey())
                .add("Description", this.getDescription())
                .add("DefaultValue", this.getDefaultValueAsString())
                .build();
    }
}
