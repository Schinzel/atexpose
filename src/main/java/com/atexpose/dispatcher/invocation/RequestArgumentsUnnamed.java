package com.atexpose.dispatcher.invocation;

import com.atexpose.api.MethodArguments;
import com.atexpose.api.datatypes.AbstractDataType;
import io.schinzel.basicutils.Checker;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Purpose of this class is to represent the request arguments.
 * <p>
 * An instance of this class transforms the request arguments from strings to objects.
 * And also sets the missing arguments to their default values.
 * <p>
 * Created by Schinzel on 2017-12-10
 */
@Accessors(prefix = "m")
class RequestArgumentsUnnamed implements IRequestArguments {
    /** The request argument values as object. */
    @Getter private final Object[] mArgumentValuesAsObjects;


    @Builder
    RequestArgumentsUnnamed(MethodArguments methodArguments, List<String> argumentValuesAsStrings) {
        Object[] argumentValuesAsObjects = castArgumentValues(argumentValuesAsStrings, methodArguments);
        mArgumentValuesAsObjects = setDefaultArgumentValues(argumentValuesAsObjects, methodArguments);
    }


    /**
     * @param argumentValuesAsStrings The strings to cast
     * @param methodArguments         The method arguments which stipulate the data type
     * @return The argument string values cast to the data type of the corresponding method argument.
     */
    static Object[] castArgumentValues(List<String> argumentValuesAsStrings, MethodArguments methodArguments) {
        Object[] argumentValuesAsObjects = ArrayUtils.EMPTY_OBJECT_ARRAY;
        if (Checker.isNotEmpty(argumentValuesAsStrings)) {
            argumentValuesAsObjects = new Object[argumentValuesAsStrings.size()];
            for (int i = 0; i < argumentValuesAsStrings.size(); i++) {
                AbstractDataType dataType = methodArguments.getArgument(i).getDataType();
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValuesAsStrings.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    /**
     * Sets the method argument default values for the request arguments that are not present.
     *
     * @param requestArgumentValues The request argument values
     * @param methodArguments       The arguments of the method
     * @return All the arguments of a method
     */
    static Object[] setDefaultArgumentValues(Object[] requestArgumentValues, MethodArguments methodArguments) {
        // Get a copy of the argument values
        Object[] argumentValues = methodArguments.getCopyOfArgumentDefaultValues();
        // If argument values where supplied
        if (Checker.isNotEmpty(requestArgumentValues)) {
            //Overwrite the default argument values with the a many argument values as were present in the request
            System.arraycopy(requestArgumentValues, 0, argumentValues, 0, requestArgumentValues.length);
        }
        return argumentValues;
    }
}
