package com.atexpose.dispatcher.invocation;

import com.atexpose.api.Argument;
import com.atexpose.api.MethodArguments;
import com.atexpose.api.datatypes.DataType;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by Schinzel on 2017-12-11
 */
public class RequestArgumentsUnnamedTest {


    private MethodArguments getMethodArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataType.STRING)
                .defaultValue("my_value")
                .build();
        Argument argument2 = Argument.builder()
                .name("arg2")
                .dataType(DataType.INT)
                .defaultValue("1234")
                .build();
        Argument argument3 = Argument.builder()
                .name("arg3")
                .dataType(DataType.BOOLEAN)
                .defaultValue("true")
                .build();
        ImmutableList<Argument> arguments = ImmutableList.of(argument1, argument2, argument3);
        return MethodArguments.create(arguments);
    }


    @Test
    public void constructor_2arguments_3arguments() {
        ImmutableList<String> argumentValuesAsStrings = ImmutableList.of("new_val", "777");
        MethodArguments methodArguments = this.getMethodArguments();
        Object[] argumentValuesAsObjects = RequestArgumentsUnnamed.builder()
                .argumentValuesAsStrings(argumentValuesAsStrings)
                .methodArguments(methodArguments)
                .build()
                .getArgumentValuesAsObjects();
        assertThat(argumentValuesAsObjects).containsExactly("new_val", 777, true);
    }


    @Test
    public void castArgumentValues_3StringArguments_TheArgumentsAsDataTypes() {
        ImmutableList<String> argumentValuesAsStrings = ImmutableList.of("new_val", "777", "false");
        MethodArguments methodArguments = this.getMethodArguments();
        Object[] objects = RequestArgumentsUnnamed
                .castArgumentValues(argumentValuesAsStrings, methodArguments);
        assertThat(objects).containsExactly("new_val", 777, false);
    }


    @Test
    public void setDefaultArgumentValues_RequestArgumentValuesNull_DefaultArguments() {
        Object[] objects = RequestArgumentsUnnamed
                .setDefaultArgumentValues(null, this.getMethodArguments());
        assertThat(objects).containsExactly("my_value", 1234, true);
    }


    @Test
    public void setDefaultArgumentValues_RequestArgumentValuesEmptyArray_DefaultArguments() {
        Object[] objects = RequestArgumentsUnnamed
                .setDefaultArgumentValues(new Object[0], this.getMethodArguments());
        assertThat(objects).containsExactly("my_value", 1234, true);
    }


    @Test
    public void setDefaultArgumentValues_RequestArgumentValuesString_DefaultArguments() {
        Object[] requestArgumentValues = {"new_value"};
        Object[] objects = RequestArgumentsUnnamed
                .setDefaultArgumentValues(requestArgumentValues, this.getMethodArguments());
        assertThat(objects).containsExactly("new_value", 1234, true);
    }


    @Test
    public void setDefaultArgumentValues_RequestArgumentValuesStringIntBool_DefaultArguments() {
        Object[] requestArgumentValues = {"new_value", 777, false};
        Object[] objects = RequestArgumentsUnnamed
                .setDefaultArgumentValues(requestArgumentValues, this.getMethodArguments());
        assertThat(objects).containsExactly("new_value", 777, false);
    }
}