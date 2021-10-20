package com.atexpose.api.data_types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The purpose of this enum is to provide the available data types.
 *
 * @author schinzel
 */
@Accessors(prefix = "m")
@AllArgsConstructor
public enum DataTypeEnum {
    ALPHA_NUMERIC_STRING(new AlphNumStringDT()),
    BOOLEAN(new BooleanDT()),
    INT(new IntDT()),
    STRING(new StringDT()),
    VOID(new VoidDT());

    @Getter
    private final AbstractDataType mDataType;


}
