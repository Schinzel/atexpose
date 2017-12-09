package com.atexpose.api;

import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.errors.ExposedInvocationException;
import com.atexpose.errors.IExceptionProperties;
import com.atexpose.errors.RuntimeError;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.str.Str;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The definition of a method. Used to invoke methods. This object can be seen
 * as an extension "java.lang.reflect.Method". Holds the name of the method, the
 * argument names, the default values of the arguments, the return type and
 * meta-data.
 */
@Accessors(prefix = "m")
public class MethodObject implements IValueWithKey, IStateNode {
    //The key of this object. Used in ValuesWithKeys collections.
    @Getter final String mKey;
    //The type of the return of the method as defined by the static variables.
    @Getter private final AbstractDataType mReturnDataType;
    //The access level required to use this method.
    @Getter private final int mAccessLevelRequiredToUseThisMethod;
    //Flag which indicates if this method requires user to be authenticated to invoke this method
    @Getter private final boolean mAuthRequired;
    //The object that is to be invoked
    private final Object mObject;
    //The method that this object defines
    private final Method mMethod;
    //Holds a description of the method.
    private final String mDescription;
    //How many of the arguments are required.
    private final int mNoOfRequiredArguments;
    //Holds the arguments of this object.
    private List<Argument> mArguments;
    //A list of labels to which this method belongs.
    private List<Label> mLabels;
    //Alias, i.e. alternate method names for this method.
    private List<Alias> mAliases = new ArrayList<>();
    //A collection for CPU efficient up look of argument position.
    private final Map<String, Integer> mArgumentPositions = new HashMap<>(20);
    // ---------------------------------
    // - CONSTRUCTOR  -
    // ---------------------------------


    @Builder
    private MethodObject(Object theObject, Method method, String description, int noOfRequiredArguments,
                         List<Argument> arguments, int accessLevel, List<Label> labels,
                         AbstractDataType returnDataType, List<Alias> aliases, boolean requireAuthentication) {
        Thrower.throwIfVarNull(method, "method");
        Thrower.throwIfTrue(noOfRequiredArguments > arguments.size())
                .message("Number of required arguments is higher than the actual number of arguments");
        String methodName = method.getName();
        this.mKey = methodName;
        mObject = theObject;
        mMethod = method;
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
    public Object invoke(List<String> argumentValues, List<String> argumentNames, int dispatcherAccessLevel) throws ExposedInvocationException {
        this.checkNumberOfArguments(argumentValues);
        this.checkAccessLevel(dispatcherAccessLevel);
        Object[] argumentValuesAsObjects = this.castArgumentValuesToUse(argumentValues, argumentNames);
        argumentValuesAsObjects = this.setDefaultArgumentValues(argumentValuesAsObjects, argumentNames);
        return MethodObject.invoke(mMethod, mObject, argumentValuesAsObjects);
    }


    static Object invoke(Method method, Object object, Object[] argumentValuesAsObjects) throws ExposedInvocationException {
        try {
            return method.invoke(object, argumentValuesAsObjects);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            StackTraceElement ste = cause.getStackTrace()[0];
            Map<String, String> properties = ImmutableMap.<String, String>builder()
                    .put("ErrorMessage", cause.getMessage())
                    .put("Method", ste.getMethodName())
                    .put("Class", ste.getClassName())
                    .put("LineNumber", String.valueOf(ste.getLineNumber()))
                    .build();
            if (ite.getCause() instanceof IExceptionProperties) {
                properties = ImmutableMap.<String, String>builder()
                        .putAll(properties)
                        .putAll(((IExceptionProperties) cause).getProperties())
                        .build();
            }
            throw new ExposedInvocationException(properties);
        } catch (IllegalAccessException iae) {
            throw new RuntimeError("Access error " + iae.toString());
        }
    }


    // ---------------------------------
    // - ARGUMENT HANDLING  -
    // ---------------------------------
    private void checkNumberOfArguments(List<String> argumentValues) {
        int noOfArgumentsInCall = 0;
        if (!Checker.isEmpty(argumentValues)) {
            noOfArgumentsInCall = argumentValues.size();
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


    private Object[] castArgumentValuesToUse(List<String> argumentValuesAsStrings, List<String> argumentNames) {
        Object[] argumentValuesAsObjects = null;
        if (argumentValuesAsStrings != null && argumentValuesAsStrings.size() > 0) {
            argumentValuesAsObjects = new Object[argumentValuesAsStrings.size()];
            AbstractDataType dataType;
            for (int i = 0; i < argumentValuesAsStrings.size(); i++) {
                if (argumentNames == null || argumentNames.size() == 0) {
                    dataType = this.getArgument(i).getDataType();
                } else {
                    dataType = this.getArgument(argumentNames.get(i)).getDataType();
                    if (dataType == null) {
                        throw new RuntimeError("Unknown data type '" + argumentNames.get(i) + "'");
                    }
                }
                argumentValuesAsObjects[i] = dataType.convertFromStringToDataType(argumentValuesAsStrings.get(i));
            }
        }
        return argumentValuesAsObjects;
    }


    private Object[] setDefaultArgumentValues(Object[] argumentValues, List<String> argumentNames) {
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
            for (int i = 0; i < argumentNames.size(); i++) {
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
    private int[] getArgumentPositions(List<String> argumentNames) {
        int[] argPositions = new int[argumentNames.size()];
        for (int i = 0; i < argumentNames.size(); i++) {
            argPositions[i] = this.getArgumentPosition(argumentNames.get(i));
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
                .addChildren("Aliases", mAliases)
                .addChildren("Arguments", mArguments)
                .addChildren("Labels", mLabels)
                .build();
    }

}
