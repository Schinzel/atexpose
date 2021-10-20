package com.atexpose.api;

import com.atexpose.api.data_types.AbstractDataType;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.str.Str;
import io.schinzel.basicutils.thrower.Thrower;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

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
    @Getter private final Object mObject;
    //The method that this object defines
    @Getter private final Method mMethod;
    //Holds a description of the method.
    @Getter private final String mDescription;
    //How many of the arguments are required.
    @Getter private final int mNoOfRequiredArguments;
    //Holds the arguments of this method
    @Getter private final MethodArguments mMethodArguments;
    //A list of labels to which this method belongs.
    private List<Label> mLabels;


    @Builder
    private MethodObject(Object theObject, Method method, String description, int noOfRequiredArguments,
                         List<Argument> arguments, int accessLevel, List<Label> labels,
                         AbstractDataType returnDataType, boolean requireAuthentication) {
        Thrower.createInstance()
                .throwIfVarNull(theObject, "theObject")
                .throwIfVarNull(method, "method")
                .throwIfVarNull(description, "description")
                .throwIfVarNull(returnDataType, "returnDataType")
                .throwIfTrue(noOfRequiredArguments > arguments.size(), "Number of required arguments is higher than the actual number of arguments");
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
        if (!Checker.isEmpty(labels)) {
            mLabels = labels;
            for (Label label : labels) {
                label.addMethod(this);
            }
            Collections.sort(mLabels);
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
        return str.asString();
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
                .addChildren("Labels", mLabels)
                .build();
    }

}
