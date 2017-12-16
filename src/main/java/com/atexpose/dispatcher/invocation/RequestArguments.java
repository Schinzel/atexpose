package com.atexpose.dispatcher.invocation;

import com.atexpose.api.MethodArguments;
import io.schinzel.basicutils.Checker;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-16
 */
@Accessors(prefix = "m")
public class RequestArguments {
    /** The request argument values as object. */
    @Getter private final Object[] mArgumentValuesAsObjects;


    @Builder
    RequestArguments(MethodArguments methodArguments,
                     List<String> requestArgumentValuesAsStrings,
                     List<String> requestArgumentNames) {
        Object[] requestArgumentValues = Checker.isEmpty(requestArgumentNames)
                ? methodArguments.cast(requestArgumentValuesAsStrings)
                : methodArguments.cast(requestArgumentValuesAsStrings, requestArgumentNames);
        int[] argumentPositions = Checker.isEmpty(requestArgumentNames)
                ? methodArguments.getArgumentPositions(requestArgumentNames)
                : IntStream.range(0, requestArgumentValues.length).toArray();
        mArgumentValuesAsObjects = getArgValues(
                methodArguments.getCopyOfArgumentDefaultValues(),
                requestArgumentValues,
                argumentPositions);
    }


    static Object[] getArgValues(Object[] defaultArgumentValues, Object[] requestArgumentValues, int[] argumentPositions) {
        // Go through the arguments array as set values
        for (int i = 0; i < requestArgumentValues.length; i++) {
            int positionInputArgument = argumentPositions[i];
            Object inputArgumentValue = requestArgumentValues[i];
            defaultArgumentValues[positionInputArgument] = inputArgumentValue;
        }
        return defaultArgumentValues;
    }


}
