package com.atexpose.api;

import com.atexpose.Expose;
import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.api.datatypes.DataType;
import com.atexpose.errors.SetUpError;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.valueswithkeys.ValuesWithKeys;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Schinzel
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
@Accessors(prefix = "m")
public class API implements IStateNode {
    @Getter private final ValuesWithKeys<MethodObject> mMethods = ValuesWithKeys.create("Methods");
    @Getter private final ValuesWithKeys<Argument> mArguments = ValuesWithKeys.create("Arguments");
    @Getter(AccessLevel.PACKAGE) final ValuesWithKeys<Label> mLabels = ValuesWithKeys.create("Labels");
    @Getter(AccessLevel.PACKAGE) private final ValuesWithKeys<Alias> mAliases = ValuesWithKeys.create("Aliases");
    private final ValuesWithKeys<AbstractDataType> mDataTypes = ValuesWithKeys.create("DataTypes");


    public API() {
        for (DataType datatype : DataType.values()) {
            this.addPrimitiveDataType(datatype.getInstance());
        }
        //Set up the basic arguments.
        this
                .addArgument(Argument.builder()
                        .name("Float")
                        .dataType(DataType.FLOAT)
                        .description("A float.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Json")
                        .dataType(DataType.JSON)
                        .description("JSON.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Int")
                        .dataType(DataType.INT)
                        .description("An integer.")
                        .defaultValue("0")
                        .build())
                .addArgument(Argument.builder()
                        .name("String")
                        .dataType(DataType.STRING)
                        .description("A string.")
                        .build())
                //Set upp method for help
                .addArgument(Argument.builder()
                        .name("SearchString")
                        .dataType(DataType.STRING)
                        .description("A string to sort for.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Options")
                        .dataType(DataType.STRING)
                        .description("Options available are v for verbose help and l for mLabels.")
                        .defaultValue("")
                        .build());
        this.addLabel("API", "Methods that involve the API.");
        this.expose(new Help(this));
    }


    private API addPrimitiveDataType(AbstractDataType adt) {
        mDataTypes.add(adt);
        return this;
    }


    public boolean methodExits(String methodName) {
        return (this.getMethodObject(methodName) != null);
    }


    public MethodObject getMethodObject(String methodName) {
        MethodObject methodObject;
        if (mMethods.has(methodName)) {
            methodObject = mMethods.get(methodName);
        }//If no method by argument name was found
        else if (mAliases.has(methodName)) {
            methodObject = mAliases.get(methodName).getMethod();
        } else {
            throw new RuntimeException("No such method '" + methodName + "'");
        }
        return methodObject;
    }


    private List<Alias> addAliases(String[] names) {
        List<Alias> aliases = new ArrayList<>();
        if (!Checker.isEmpty(names)) {
            for (String name : names) {
                Alias alias = new Alias(name);
                aliases.add(alias);
                this.mAliases.add(alias);
            }
        }
        return aliases;
    }


    public API addLabel(String name, String description) {
        Label label = new Label(name, description);
        mLabels.add(label);
        return this;
    }


    public API addArgument(Argument arg) {
        mArguments.add(arg);
        return this;
    }
    // ------------------------------------
    // - EXPOSE
    // ------------------------------------


    /**
     * Exposes a created instance. Both static and non-static methods can be
     * invoked.
     *
     * @param theObject The instance to expose.
     * @return This for chaining.
     */
    public API expose(Object theObject) {
        expose(theObject.getClass(), theObject);
        return this;
    }


    /**
     * Exposes a class. Only static methods can be invoked.
     *
     * @param theClass The class to expose.
     * @return This for chaining.
     */
    public API expose(Class theClass) {
        Object theObject;
        try {
            theObject = theClass.newInstance();
        } catch (InstantiationException ex) {
            throw new SetUpError("Could not instantiate the class '" + theClass.getSimpleName() + "'");
        } catch (IllegalAccessException ex) {
            throw new SetUpError("Could not access the class '" + theClass.getSimpleName() + "'");
        }
        expose(theClass, theObject);
        return this;
    }


    private void expose(Class theClass, Object theObject) {
        Method[] methods = theClass.getDeclaredMethods();
        for (Method method : methods) {
            Expose expose = method.getAnnotation(Expose.class);
            if (expose != null) {
                try {
                    List<Argument> arguments = this.mArguments.get(Arrays.asList(expose.arguments()));
                    List<Label> labels = this.mLabels.get(Arrays.asList(expose.labels()));
                    List<Alias> aliases = addAliases(expose.aliases());
                    AbstractDataType returnDataType = mDataTypes.get(method.getReturnType().getSimpleName());
                    MethodObject methodObject = MethodObject.builder()
                            .theObject(theObject)
                            .method(method)
                            .description(expose.description())
                            .noOfRequiredArguments(expose.requiredArgumentCount())
                            .arguments(arguments)
                            .accessLevel(expose.requiredAccessLevel())
                            .labels(labels)
                            .returnDataType(returnDataType)
                            .aliases(aliases)
                            .build();
                    mMethods.add(methodObject);
                } catch (Exception e) {
                    throw new SetUpError("Error when setting up method '" + method.getName() + "' in class '" + theObject.getClass().getSimpleName() + "'. " + e.getMessage());
                }
            }
        }
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .addChildren(mDataTypes)
                .addChildren(mArguments)
                .addChildren(mLabels)
                .addChildren(mMethods)
                .build();
    }


}
