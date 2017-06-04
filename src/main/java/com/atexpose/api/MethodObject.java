package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.collections.keyvalues.IValueKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.str.Str;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The definition of a method. Used to invoke methods. This object can be seen
 * as an extension "java.lang.reflect.Method". Holds the name of the method, the
 * argument names, the default values of the arguments, the return type and
 * meta-data.
 */
@Accessors(prefix = "m")
public class MethodObject implements IValueKey, IStateNode {
    @Getter final String mKey;
    //The type of the return of the method as defined by the static variables.
    @Getter private final AbstractDataType mReturnDataType;
    //The access level required to use this method.
    @Getter private final int mAccessLevelRequiredToUseThisMethod;
    //The object that is to be invoked
    private final Object mObject;
    //The method that this object defines
    private final Method mMethod;
    //Holds a description of the method.
    private final String mDescription;
    //How many of the arguments are required.
    private final int mNoOfRequiredArguments;
    //Holds the arguments of this object.
    private List<Argument> mArguments = EmptyObjects.EMPTY_LIST;
    //A list of labels to which this method belongs.
    private List<Label> mLabels;
    private List<Alias> mAliases = new ArrayList<>();
    //A collection for CPU efficient up look of argument position.
    private final HashMap<String, Integer> mArgumentPositions = new HashMap<>(20);

    @Getter private final boolean mAuthRequired;
    // ---------------------------------
    // - CONSTRUCTOR  -
    // ---------------------------------


    @Builder
    private MethodObject(Object theObject, Method method, String description, int noOfRequiredArguments,
                         List<Argument> arguments, int accessLevel, List<Label> labels,
                         AbstractDataType returnDataType, List<Alias> aliases, boolean requireAuthentication) {
        String methodName = method.getName();
        this.mKey = methodName;
        mObject = theObject;
        mMethod = method;
        Thrower.throwIfVarNull(mMethod, "Error in setting up method '" + theObject.getClass().getName() + "." + methodName + "'. Class not found.");
        mDescription = description;
        mMethod.setAccessible(true);
        mReturnDataType = returnDataType;
        mNoOfRequiredArguments = noOfRequiredArguments;
        mAccessLevelRequiredToUseThisMethod = accessLevel;
        mArguments = arguments;
        mAuthRequired = requireAuthentication;
        //Put arguments and its aliases in a hash map for quick up look of argument position
        int argumentPosition = 0;
        for (Argument argument : mArguments) {
            mArgumentPositions.put(argument.getKey(), argumentPosition);
            if (!Checker.isEmpty(mAliases)) {
                for (String alias : argument.getAliases()) {
                    mArgumentPositions.put(alias, argumentPosition);
                }
            }
            argumentPosition++;
        }
        if (!Checker.isEmpty(aliases)) {
            mAliases = aliases;
            for (Alias alias : mAliases) {
                alias.setMethod(this);
            }
            Collections.sort(mAliases);
        }
        if (!Checker.isEmpty(labels)) {
            mLabels = labels;
            for (Label label : labels) {
                label.addMethod(this);
            }
            Collections.sort(mLabels);
        }
    }


    // ---------------------------------
    // - INVOKING  -
    // ---------------------------------
    public Object invoke(String[] argumentValues, String[] argumentNames, int dispatcherAccessLevel) {
        this.checkNumberOfArguments(argumentValues);
        this.checkAccessLevel(dispatcherAccessLevel);
        Object[] argumentValuesAsObjects = this.castArgumentValuesToUse(argumentValues, argumentNames);
        argumentValuesAsObjects = setDefaultArgumentValues(argumentValuesAsObjects, argumentNames);
        Object returnObject;
        try {
            returnObject = mMethod.invoke(mObject, argumentValuesAsObjects);
        } catch (InvocationTargetException ite) {
            StackTraceElement ste = ite.getCause().getStackTrace()[0];
            Str str = Str.create()
                    .a("Message: ").anl(ite.getCause().getMessage())
                    .a("Class: ").anl(ste.getClassName())
                    .a("Method: ").anl(ste.getMethodName())
                    .a("Line num: ").a(ste.getLineNumber());
            throw new RuntimeError(str.getString());
        } catch (IllegalAccessException iae) {
            throw new RuntimeError("Access error " + iae.toString());
        }
        return returnObject;
    }


    // ---------------------------------
    // - ARGUMENT HANDLING  -
    // ---------------------------------
    private void checkNumberOfArguments(String[] argumentValues) {
        int noOfArgumentsInCall = 0;
        if (!Checker.isEmpty(argumentValues)) {
            noOfArgumentsInCall = argumentValues.length;
        }
        StringBuilder errorText = new StringBuilder();
        String argumentSingular = "argument";
        String argumentPlural = "arguments";
        if (noOfArgumentsInCall < this.mNoOfRequiredArguments) {
            errorText.append("Too few arguments. Was ").append(noOfArgumentsInCall).append(" and method ")
                    .append(this.getKey()).append(" requires a minimum of ").append(this.mNoOfRequiredArguments).append(" ");
            if (this.mNoOfRequiredArguments == 1) {
                errorText.append(argumentSingular);
            } else {
                errorText.append(argumentPlural);
            }
            errorText.append(".");
        }
        if (noOfArgumentsInCall > this.mArguments.size()) {
            errorText.append("Too many arguments. Was ").append(noOfArgumentsInCall).append(" and method ")
                    .append(this.getKey()).append(" takes a maximum of ").append(this.mArguments.size()).append(" ");
            if (this.mArguments.size() == 1) {
                errorText.append(argumentSingular);
            } else {
                errorText.append(argumentPlural);
            }
            errorText.append(".");
        }
        if (!errorText.toString().isEmpty()) {
            throw new RuntimeError(errorText.toString());
        }
    }


    private Object[] castArgumentValuesToUse(String[] argumentValuesAsStrings, String[] argumentNames) {
        Object[] argumentValuesAsObjects = null;
        if (argumentValuesAsStrings != null && argumentValuesAsStrings.length > 0) {
            argumentValuesAsObjects = new Object[argumentValuesAsStrings.length];
            AbstractDataType dataType;
            for (int i = 0; i < argumentValuesAsStrings.length; i++) {
                if (argumentNames == null || argumentNames.length == 0) {
                    dataType = this.getArgument(i).getDataType();
                } else {
                    dataType = this.getArgument(argumentNames[i]).getDataType();
                    if (dataType == null) {
                        throw new RuntimeError("Unknown data type '" + argumentNames[i] + "'");
                    }
                }
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValuesAsStrings[i]);
            }
        }
        return argumentValuesAsObjects;
    }


    private Object[] setDefaultArgumentValues(Object[] argumentValues, String[] argumentNames) {
        // Get a copy of the argument values
        Object[] argumentDefaultValues = this.getCopyOfArgumentDefaultValues();
        // If no argument names were supplied
        if (Checker.isEmpty(argumentNames)) {
            // If argument values where supplied
            if (argumentValues != null) {
                System.arraycopy(argumentValues, 0, argumentDefaultValues, 0, argumentValues.length);
            }
        }// else, i.e. argument names were supplied
        else {
            // Get the argument positions
            int[] argumentPositions = this.getArgumentPositions(argumentNames);
            Object inputArgumentValue;
            int positionInputArgument;
            // Go through the arguments array as set values
            for (int i = 0; i < argumentNames.length; i++) {
                positionInputArgument = argumentPositions[i];
                inputArgumentValue = argumentValues[i];
                argumentDefaultValues[positionInputArgument] = inputArgumentValue;
            }
        }
        return argumentDefaultValues;
    }


    private Argument getArgument(String argName) {
        return mArguments.get(getArgumentPosition(argName));
    }


    private Argument getArgument(int pos) {
        return mArguments.get(pos);
    }


    /**
     * Return the positions of a set of argument names in the method call of the
     * held method.
     */
    private int[] getArgumentPositions(String[] argumentNames) {
        int[] argPositions = new int[argumentNames.length];
        for (int i = 0; i < argumentNames.length; i++) {
            argPositions[i] = this.getArgumentPosition(argumentNames[i]);
        }
        return argPositions;
    }


    /**
     * Return the position of a single argument name.
     */
    private int getArgumentPosition(String argumentName) {
        Thrower.throwIfFalse(mArgumentPositions.containsKey(argumentName), "No such argument named '" + argumentName + "' in method " + this.getKey());
        return mArgumentPositions.get(argumentName);
    }


    /**
     * Returns a copy if the default values.
     */
    private Object[] getCopyOfArgumentDefaultValues() {
        if (Checker.isEmpty(mArguments)) {
            return EmptyObjects.EMPTY_OBJECT_ARRAY;
        } else {
            Object[] returnObjects;
            returnObjects = new Object[mArguments.size()];
            for (int i = 0; i < mArguments.size(); i++) {
                returnObjects[i] = mArguments.get(i).getDefaultValue();

            }
            return returnObjects;
        }
    }


    private void checkAccessLevel(int accessLevelOfDispatcher) {
        if (accessLevelOfDispatcher < this.mAccessLevelRequiredToUseThisMethod) {
            throw new RuntimeException("The method '" + this.getKey()
                    + "' has access level " + this.mAccessLevelRequiredToUseThisMethod
                    + ". The Dispatcher used only has access to level " + accessLevelOfDispatcher
                    + " methods and below.");
        }
    }


    /**
     * Sample return
     * String help([String SearchString], [String Options])
     *
     * @return The syntax of the method
     */
    String getSyntax() {
        Str str = Str.create().asp(mReturnDataType.getKey())
                .a(this.getKey()).a("(");
        for (int i = 0; i < mArguments.size(); i++) {
            if (i > 0) {
                str.a(", ");
            }
            if (i >= this.mNoOfRequiredArguments) {
                str.a('[');
            }
            str.asp(mArguments.get(i).getDataType().getKey());
            str.a(mArguments.get(i).getKey());
            if (i >= this.mNoOfRequiredArguments) {
                str.a(']');
            }
        }
        str.a(")");
        return str.getString();
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", this.getKey())
                .add("Return", mReturnDataType.getKey())
                .add("Description", mDescription)
                .add("AccessLevelRequired", this.getAccessLevelRequiredToUseThisMethod())
                .add("RequiredArgumentsCount", mNoOfRequiredArguments)
                .add("JavaClass", mObject.getClass().getCanonicalName())
                .addChildren("Arguments", mArguments)
                .addChildren("Labels", mLabels)
                .build();
    }

}
