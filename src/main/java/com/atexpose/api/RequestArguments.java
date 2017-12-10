package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
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
            for (int i = 0; i < argumentValuesAsStrings.size(); i++) {
                AbstractDataType dataType = Checker.isEmpty(argumentNames)
                        ? methodArguments.getArgument(i).getDataType()
                        : methodArguments.getArgument(argumentNames.get(i)).getDataType();
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValuesAsStrings.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    private static Object[] setDefaultArgumentValues(MethodArguments methodArguments, Object[] requestArgumentValues, List<String> requestArgumentNames) {
        // Get a copy of the argument values
        Object[] argumentValues = methodArguments.getCopyOfArgumentDefaultValues();
        // If no argument names were supplied
        if (Checker.isEmpty(requestArgumentNames)) {
            // If argument values where supplied
            if (requestArgumentValues != null) {
                //Overwrite the default argument values with the a many argument values as were present in the request
                System.arraycopy(requestArgumentValues, 0, argumentValues, 0, requestArgumentValues.length);
            }
        }// else, i.e. argument names were supplied
        else {
            // Get the argument positions
            int[] argumentPositions = methodArguments.getArgumentPositions(requestArgumentNames);
            Object inputArgumentValue;
            int positionInputArgument;
            // Go through the arguments array as set values
            for (int i = 0; i < requestArgumentNames.size(); i++) {
                positionInputArgument = argumentPositions[i];
                inputArgumentValue = requestArgumentValues[i];
                argumentValues[positionInputArgument] = inputArgumentValue;
            }
        }
        return argumentValues;
    }
}
