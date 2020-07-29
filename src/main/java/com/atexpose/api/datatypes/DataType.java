package com.atexpose.api.datatypes;

/**
 * The purpose of this enum is to provide the available data types.
 *
 * @author schinzel
 */
public enum DataType {
    ALPHA_NUMERIC_STRING(new AlphNumStringDT()),
    BOOLEAN(new BooleanDT()),
    INT(new IntDT()),
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
        return this.getInstance().getKey();
    }
}
