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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The definition of a method. Used to invoke methods. This class can be seen
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
    //Holds the arguments of this method
    private MethodArguments mMethodArguments;
    //A list of labels to which this method belongs.
    private List<Label> mLabels;
    //Alias, i.e. alternate method names for this method.
    private List<Alias> mAliases = new ArrayList<>();


    @Builder
    private MethodObject(Object theObject, Method method, String description, int noOfRequiredArguments,
                         List<Argument> arguments, int accessLevel, List<Label> labels,
                         AbstractDataType returnDataType, List<Alias> aliases, boolean requireAuthentication) {
        Thrower.throwIfVarNull(theObject, "theObject");
        Thrower.throwIfVarNull(method, "method");
        Thrower.throwIfVarNull(description, "description");
        Thrower.throwIfVarNull(returnDataType, "returnDataType");
        Thrower.throwIfTrue(noOfRequiredArguments > arguments.size())
                .message("Number of required arguments is higher than the actual number of arguments");
        this.mKey = method.getName();
        mObject = theObject;
        mMethod = method;
        mDescription = description;
        mMethod.setAccessible(true);
        mReturnDataType = returnDataType;
        mNoOfRequiredArguments = noOfRequiredArguments;
        mAccessLevelRequiredToUseThisMethod = accessLevel;
        mMethodArguments = MethodArguments.create(arguments);
        mAuthRequired = requireAuthentication;
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


    /**
     * @param argumentValuesAsStrings The argument values of the request.
     * @param argumentNames           The names of the arguments in the request. Is empty if unnamed arguments
     *                                are used. If is not empty is synced with the argument values.
     * @param dispatcherAccessLevel   The access level of the dispatcher which is invoking this method
     * @return The response of the invoked method
     * @throws ExposedInvocationException Exception thrown if invoked method throws an error
     */
    public Object invoke(List<String> argumentValuesAsStrings, List<String> argumentNames, int dispatcherAccessLevel) throws ExposedInvocationException {
        validateArgumentCount(argumentValuesAsStrings, mNoOfRequiredArguments, mMethodArguments.size());
        validateAccessLevel(dispatcherAccessLevel, this.mAccessLevelRequiredToUseThisMethod, mKey);
        Object[] argumentValuesAsObjects = RequestArguments.builder()
                .methodArguments(mMethodArguments)
                .argumentValuesAsStrings(argumentValuesAsStrings)
                .argumentNames(argumentNames)
                .build()
                .getMArgumentValuesAsObjects();
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


    private static void validateArgumentCount(List<String> arguments, int minNumOfArgs, int maxNumOfArgs) {
        int noOfArgumentsInCall = Checker.isEmpty(arguments) ? 0 : arguments.size();
        boolean tooFewArguments = noOfArgumentsInCall < minNumOfArgs;
        boolean tooManyArguments = noOfArgumentsInCall > maxNumOfArgs;
        Thrower.throwIfTrue(tooFewArguments || tooManyArguments)
                .message("Incorrect number of arguments. Was " + noOfArgumentsInCall + ". Min is " + minNumOfArgs + " and max is " + maxNumOfArgs + ".");
    }


    private static void validateAccessLevel(int accessLevelOfDispatcher, int requiredAccessLevel, String methodName) {
        Thrower.throwIfTrue(accessLevelOfDispatcher < requiredAccessLevel)
                .message("The method '" + methodName + "' has access level " + requiredAccessLevel
                        + ". The Dispatcher used only has access to level " + accessLevelOfDispatcher
                        + " methods and below.");
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
        for (int i = 0; i < mMethodArguments.size(); i++) {
            if (i > 0) {
                str.a(", ");
            }
            if (i >= this.mNoOfRequiredArguments) {
                str.a('[');
            }
            str.asp(mMethodArguments.getArgument(i).getDataType().getKey());
            str.a(mMethodArguments.getArgument(i).getKey());
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
                .addChild("Arguments", mMethodArguments)
                .addChildren("Aliases", mAliases)
                .addChildren("Labels", mLabels)
                .build();
    }

}
