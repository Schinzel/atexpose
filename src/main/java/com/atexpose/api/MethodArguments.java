package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.google.common.collect.ImmutableList;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose of this class is hold the names and the order of the arguments of a method.
 * <p>
 * Created by Schinzel on 2017-12-09
 */
public class MethodArguments implements IStateNode {
    //Holds the arguments of this object
    private final ImmutableList<Argument> mArguments;
    //A collection for CPU efficient up look of argument position
    private final Map<String, Integer> mArgumentPositions = new HashMap<>(20);
    private static final MethodArguments EMPTY = new MethodArguments(Collections.emptyList());


    public static MethodArguments create(List<Argument> arguments) {
        return Checker.isEmpty(arguments)
                ? EMPTY
                : new MethodArguments(arguments);
    }


    private MethodArguments(List<Argument> arguments) {
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
     * @return A copy if the default values
     */
    public Object[] getCopyOfArgumentDefaultValues() {
        if (Checker.isEmpty(mArguments)) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        } else {
            Object[] returnObjects = new Object[mArguments.size()];
            for (int i = 0; i < mArguments.size(); i++) {
                returnObjects[i] = mArguments.get(i).getDefaultValue();
            }
            return returnObjects;
        }
    }


    public int size() {
        return mArguments.size();
    }


    /**
     * @param argumentValues A list of argument values as strings
     * @return The argument values cast to to the arguments' data types
     */
    public Object[] cast(List<String> argumentValues) {
        return this.cast(argumentValues, Collections.emptyList());
    }


    /**
     * @param argumentValues A list of argument values as strings
     * @param argumentNames  The names fo the argument values
     * @return The argument values cast to to the arguments' data types
     */
    public Object[] cast(List<String> argumentValues, List<String> argumentNames) {
        Thrower.throwIfVarNull(argumentValues, "argumentValues");
        Thrower.throwIfVarNull(argumentNames, "argumentNames");
        Thrower.throwIfTrue(argumentValues.isEmpty() && argumentValues.size() != argumentNames.size())
                .message("ArgumentValues and ArgumentNames need to be of same size");
        Object[] argumentValuesAsObjects = ArrayUtils.EMPTY_OBJECT_ARRAY;
        if (argumentValues != null && argumentValues.size() > 0) {
            argumentValuesAsObjects = new Object[argumentValues.size()];
            for (int i = 0; i < argumentValues.size(); i++) {
                AbstractDataType dataType = Checker.isEmpty(argumentNames)
                        ? mArguments.get(i).getDataType()
                        : this.getArgument(argumentNames.get(i)).getDataType();
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValues.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    /**
     * @param argumentPosition The position the argument name in the method signature.
     * @return Argument with the argument position
     */
    public Argument getArgument(int argumentPosition) {
        Thrower.throwIfTrue(argumentPosition < 0 || argumentPosition > mArguments.size())
                .message("Requested argument position '" + argumentPosition + "' is out of bounds. " +
                        "Position has to be between 0 and " + mArguments.size());
        return mArguments.get(argumentPosition);
    }


    /**
     * @param argumentName The name of the argument of return
     * @return Argument with the argument name
     */
    Argument getArgument(String argumentName) {
        return mArguments.get(this.getArgumentPosition(argumentName));
    }


    /**
     * @param argumentName The name of the argument
     * @return The position of an argument with the argument name
     */
    int getArgumentPosition(String argumentName) {
        Thrower.throwIfVarEmpty(argumentName, "argumentName");
        Thrower.throwIfFalse(mArgumentPositions.containsKey(argumentName))
                .message("No argument named '" + argumentName + "'");
        return mArgumentPositions.get(argumentName);
    }


    /**
     * @param argumentNames A list of argument names.
     * @return The positions of a set of argument names in the method call of the
     * held method.
     */
    public int[] getArgumentPositions(List<String> argumentNames) {
        Thrower.throwIfVarNull(argumentNames, "argumentNames");
        if (argumentNames.isEmpty()) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
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
