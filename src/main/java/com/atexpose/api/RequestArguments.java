package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.Checker;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Purpose of this class is to represent the request arguments as objects.
 * <p>
 * An instance of this class transforms the request arguments from strings to objects.
 * And also sets the missing arguments to their default values.
 * <p>
 * Created by Schinzel on 2017-12-09
 */
class RequestArguments {
    /** The request argument values as object. */
    @Getter private final Object[] mArgumentValuesAsObjects;


    @Builder
    RequestArguments(MethodArguments methodArguments, List<String> argumentValuesAsStrings, List<String> argumentNames) {
        Object[] argumentValuesAsObjects = castArgumentValuesToUse(methodArguments, argumentValuesAsStrings, argumentNames);
        mArgumentValuesAsObjects = setDefaultArgumentValues(methodArguments, argumentValuesAsObjects, argumentNames);
    }


    private static Object[] castArgumentValuesToUse(MethodArguments methodArguments, List<String> argumentValuesAsStrings, List<String> argumentNames) {
        Object[] argumentValuesAsObjects = null;
        if (argumentValuesAsStrings != null && argumentValuesAsStrings.size() > 0) {
            argumentValuesAsObjects = new Object[argumentValuesAsStrings.size()];
            AbstractDataType dataType;
            for (int i = 0; i < argumentValuesAsStrings.size(); i++) {
                if (Checker.isEmpty(argumentNames)) {
                    dataType = methodArguments.getArgument(i).getDataType();
                } else {
                    dataType = methodArguments.getArgument(argumentNames.get(i)).getDataType();
                    if (dataType == null) {
                        throw new RuntimeError("Unknown data type '" + argumentNames.get(i) + "'");
                    }
                }
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValuesAsStrings.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    private static Object[] setDefaultArgumentValues(MethodArguments methodArguments, Object[] argumentValues, List<String> argumentNames) {
        // Get a copy of the argument values
        Object[] argumentDefaultValues = methodArguments.getCopyOfArgumentDefaultValues();
        // If no argument names were supplied
        if (Checker.isEmpty(argumentNames)) {
            // If argument values where supplied
            if (argumentValues != null) {
                System.arraycopy(argumentValues, 0, argumentDefaultValues, 0, argumentValues.length);
            }
        }// else, i.e. argument names were supplied
        else {
            // Get the argument positions
            int[] argumentPositions = methodArguments.getArgumentPositions(argumentNames);
            Object inputArgumentValue;
            int positionInputArgument;
            // Go through the arguments array as set values
            for (int i = 0; i < argumentNames.size(); i++) {
                positionInputArgument = argumentPositions[i];
                inputArgumentValue = argumentValues[i];
                argumentDefaultValues[positionInputArgument] = inputArgumentValue;
            }
        }
        return argumentDefaultValues;
    }
}
