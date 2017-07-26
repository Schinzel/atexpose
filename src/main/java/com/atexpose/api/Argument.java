package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.api.datatypes.DataType;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.namedvalues.INamedValue;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Accessors(prefix = "m")
public class Argument implements INamedValue, IStateNode {
    @Getter private final String mName;
    @Getter private final String mDescription;
    //The data type of the argument
    @Getter private final AbstractDataType mDataType;
    // Holds the aliases for this argument.
    @Getter private List<String> mAliases = Collections.emptyList();
    // Holds the default value for this argument.
    @Getter private Object mDefaultValue = null;
    // Holds the string representation of the default value for this argument.
    @Getter private final String mDefaultValueAsString;


    @Builder
    Argument(String name, String description, DataType dataType, String defaultValue, String... aliases) {
        this.mName = name;
        this.mDescription = description;
        mDataType = dataType.getInstance();
        if (!Checker.isEmpty(aliases)) {
            mAliases = Arrays.asList(aliases);
            Collections.sort(mAliases);
        }
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
                .add("Name", this.getName())
                .add("DataType", this.getDataType().getName())
                .add("Description", this.getDescription())
                .add("DefaultValue", this.getDefaultValueAsString())
                .build();
    }
}
