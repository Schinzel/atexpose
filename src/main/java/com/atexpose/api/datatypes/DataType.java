package com.atexpose.api.datatypes;

/**
 * The purpose of this enum is to provide the available data types.
 *
 * @author schinzel
 */
public enum DataType {
    ALPHNUMSTRING(new AlphNumStringDT()),
    BOOLEAN(new BooleanDT()),
    FLOAT(new FloatDT()),
    INT(new IntDT()),
    JSON(new JsonObjectDT()),
    STRING(new StringDT());

    private final AbstractDataType mAdt;


    DataType(AbstractDataType adt) {
        mAdt = adt;
    }


    public AbstractDataType getInstance() {
        return mAdt;
    }


    /**
     * @return The name of the data type.
     */
    public String getName() {
        return this.getInstance().getName();
    }
}
