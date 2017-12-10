package com.atexpose.api;

import com.google.common.collect.ImmutableList;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose of this class is hold the names and the order of the arguments of a method.
 * <p>
 * Created by Schinzel on 2017-12-09
 */
class MethodArguments implements IStateNode {
    //Holds the arguments of this object
    private final ImmutableList<Argument> mArguments;
    //A collection for CPU efficient up look of argument position
    private final Map<String, Integer> mArgumentPositions = new HashMap<>(20);


    MethodArguments(List<Argument> arguments) {
        Thrower.throwIfVarNull(arguments, "arguments");
        //Put arguments and its aliases in a hash map for quick up look of argument position
        int argumentPosition = 0;
        for (Argument argument : arguments) {
            mArgumentPositions.put(argument.getKey(), argumentPosition);
            if (!Checker.isEmpty(argument.getAliases())) {
                for (String argumentAlias : argument.getAliases()) {
                    mArgumentPositions.put(argumentAlias, argumentPosition);
                }
            }
            argumentPosition++;
        }
        mArguments = ImmutableList.copyOf(arguments);
    }


    /**
     * Returns a copy if the default values.
     */
    Object[] getCopyOfArgumentDefaultValues() {
        if (Checker.isEmpty(mArguments)) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        } else {
            Object[] returnObjects;
            returnObjects = new Object[mArguments.size()];
            for (int i = 0; i < mArguments.size(); i++) {
                returnObjects[i] = mArguments.get(i).getDefaultValue();

            }
            return returnObjects;
        }
    }


    int size() {
        return mArguments.size();
    }


    /**
     * @param argumentName The name of the argument of return
     * @return Argument with the argument name
     */
    Argument getArgument(String argumentName) {
        return mArguments.get(this.getArgumentPosition(argumentName));
    }


    /**
     * @param argumentPosition The position the argument name in the method signature.
     * @return Argument with the argument position
     */
    Argument getArgument(int argumentPosition) {
        return mArguments.get(argumentPosition);
    }


    /**
     * @param argumentName The name of the argument
     * @return The position of an argument with the argument name
     */
    private int getArgumentPosition(String argumentName) {
        Thrower.throwIfFalse(mArgumentPositions.containsKey(argumentName))
                .message("No argument named '" + argumentName + "'");
        return mArgumentPositions.get(argumentName);
    }


    /**
     * Return the positions of a set of argument names in the method call of the
     * held method.
     */
    int[] getArgumentPositions(List<String> argumentNames) {
        int[] argPositions = new int[argumentNames.size()];
        for (int i = 0; i < argumentNames.size(); i++) {
            argPositions[i] = this.getArgumentPosition(argumentNames.get(i));
        }
        return argPositions;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .addChildren("Arguments", mArguments)
                .build();
    }
}
