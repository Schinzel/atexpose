package com.atexpose.dispatcher.invocation;

import com.atexpose.api.MethodArguments;
import io.schinzel.basicutils.Thrower;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

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
class RequestArgumentsNamed implements IRequestArguments {
    /** The request argument values as object. */
    @Getter private final Object[] mArgumentValuesAsObjects;


    @Builder
    RequestArgumentsNamed(MethodArguments methodArguments, List<String> argumentValuesAsStrings, List<String> argumentNames) {
        Object[] argumentValuesAsObjects = castArgumentValuesToUse(methodArguments, argumentValuesAsStrings, argumentNames);
        mArgumentValuesAsObjects = setDefaultArgumentValues(methodArguments, argumentValuesAsObjects, argumentNames);
    }


    private static Object[] castArgumentValuesToUse(MethodArguments methodArguments,
                                                    List<String> argumentValuesAsStrings,
                                                    List<String> argumentNames) {
        Object[] argumentValuesAsObjects = null;
        if (argumentValuesAsStrings != null && argumentValuesAsStrings.size() > 0) {
            argumentValuesAsObjects = new Object[argumentValuesAsStrings.size()];
            for (int i = 0; i < argumentValuesAsStrings.size(); i++) {
                argumentValuesAsObjects[i] = methodArguments
                        .getArgument(argumentNames.get(i))
                        .getDataType()
                        .convertFromStringToDataType(argumentValuesAsStrings.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    static Object[] setDefaultArgumentValues(MethodArguments methodArguments,
                                             Object[] requestArgumentValues,
                                             List<String> requestArgumentNames) {
        Thrower.throwIfNull(requestArgumentValues, "requestArgumentValues");
        Thrower.throwIfNull(requestArgumentNames, "requestArgumentNames");
        Thrower.throwIfTrue(requestArgumentValues.length != requestArgumentNames.size())
                .message("The number of request argument values and names must be equal.");
        // Get a copy of the argument values
        Object[] argumentValues = methodArguments.getCopyOfArgumentDefaultValues();
        // Get the argument positions
        int[] argumentPositions = methodArguments.getArgumentPositions(requestArgumentNames);
        // Go through the arguments array as set values
        for (int i = 0; i < requestArgumentNames.size(); i++) {
            int positionInputArgument = argumentPositions[i];
            Object inputArgumentValue = requestArgumentValues[i];
            argumentValues[positionInputArgument] = inputArgumentValue;
        }
        return argumentValues;
    }
}
