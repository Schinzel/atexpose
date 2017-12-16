package com.atexpose.dispatcher.invocation;

import lombok.Builder;
import lombok.Getter;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-16
 */
public class R2 {
    /** The request argument values as object. */
    @Getter private final Object[] mArgumentValuesAsObjects;


    @Builder
    R2(Object[] defaultArgumentValues, Object[] requestArgumentValues, int[] argumentPositions) {
        // Go through the arguments array as set values
        for (int i = 0; i < requestArgumentValues.length; i++) {
            int positionInputArgument = argumentPositions[i];
            Object inputArgumentValue = requestArgumentValues[i];
            defaultArgumentValues[positionInputArgument] = inputArgumentValue;
        }
        mArgumentValuesAsObjects = defaultArgumentValues;

    }

}
