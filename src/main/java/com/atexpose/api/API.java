package com.atexpose.api;

import com.atexpose.Expose;
import com.atexpose.api.datatypes.AbstractDataType;
import com.atexpose.api.datatypes.ClassDT;
import com.atexpose.api.datatypes.DataTypeEnum;
import com.atexpose.errors.SetUpError;
import io.schinzel.basicutils.collections.valueswithkeys.ValuesWithKeys;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
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
    @Getter private final ValuesWithKeys<AbstractDataType> mDataTypes = ValuesWithKeys.create("DataTypes");


    public API() {
        for (DataTypeEnum datatype : DataTypeEnum.values()) {
            mDataTypes.add(datatype.getDataType());
        }
        //Set up the basic arguments.
        this
                .addArgument(Argument.builder()
                        .name("Int")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("An integer")
                        .defaultValue("0")
                        .build())
                .addArgument(Argument.builder()
                        .name("String")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A string")
                        .build())
                //Set upp method for help
                .addArgument(Argument.builder()
                        .name("SearchString")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A string to search for")
                        .build())
                .addArgument(Argument.builder()
                        .name("Options")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("Options available are v for verbose help and l for labels.")
                        .defaultValue("")
                        .build());
        this.addLabel("API", "Methods that involve the API.");
        this.expose(new Help(this));
    }


    public MethodObject getMethodObject(String methodName) {
        if (!mMethods.has(methodName)) {
            throw new RuntimeException("No such method '" + methodName + "'");
        }
        return mMethods.get(methodName);
    }


    public API addLabel(String name, String description) {
        Label label = new Label(name, description);
        mLabels.add(label);
        return this;
    }


    public API addDataType(AbstractDataType dataType) {
        mDataTypes.add(dataType);
        return this;
    }


    public API addDataType(Class<?> clazz) {
        mDataTypes.add(new ClassDT<>(clazz));
        return this;
    }

    public API addArgument(Argument arg) {
        mArguments.add(arg);
        return this;
    }


    public API addArgument(String name, AbstractDataType dataType, String description) {
        Argument argument = Argument.builder()
                .name(name)
                .dataType(dataType)
                .description(description)
                .build();
        return this.addArgument(argument);
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
        return this.expose(theObject.getClass(), theObject);
    }


    /**
     * Exposes a class. Only static methods can be invoked.
     *
     * @param theClass The class to expose.
     * @return This for chaining.
     */
    public API expose(Class<?> theClass) {
        Object theObject;
        try {
            theObject = theClass.newInstance();
        } catch (InstantiationException ex) {
            throw new SetUpError("Could not instantiate the class '" + theClass.getSimpleName() + "'");
        } catch (IllegalAccessException ex) {
            throw new SetUpError("Could not access the class '" + theClass.getSimpleName() + "'");
        }
        return this.expose(theClass, theObject);
    }


    private API expose(Class<?> theClass, Object theObject) {
        Method[] methods = theClass.getDeclaredMethods();
        for (Method method : methods) {
            Expose expose = method.getAnnotation(Expose.class);
            if (expose != null) {
                try {
                    List<Argument> arguments = mArguments.get(Arrays.asList(expose.arguments()));
                    List<Label> labels = mLabels.get(Arrays.asList(expose.labels()));
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
                            .build();
                    mMethods.add(methodObject);
                } catch (Exception e) {
                    throw new SetUpError("Error when setting up method '" + method.getName() + "' in class '" + theObject.getClass().getSimpleName() + "'. " + e.getMessage());
                }
            }
        }
        return this;
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
