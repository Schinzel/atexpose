package com.atexpose.dispatcher.invocation;

import com.atexpose.api.Argument;
import com.atexpose.api.MethodArguments;
import com.atexpose.api.datatypes.DataType;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by Schinzel on 2017-12-14
 */
public class RequestArgumentsNamedTest {


    private MethodArguments getMethodArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataType.STRING)
                .defaultValue("my_val")
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
    public void setDefaultArgumentValues_requestArgumentValueNull_Exception() {
        Object[] requestArgumentValues = null;
        List<String> requestArgumentNames = ImmutableList.of("", "");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                RequestArgumentsNamed.setDefaultArgumentValues(
                        this.getMethodArguments(),
                        requestArgumentValues,
                        requestArgumentNames));
    }


    @Test
    public void setDefaultArgumentValues_requestArgumentNamesNull_Exception() {
        Object[] requestArgumentValues = new Object[0];
        List<String> requestArgumentNames = null;
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                RequestArgumentsNamed.setDefaultArgumentValues(
                        this.getMethodArguments(),
                        requestArgumentValues,
                        requestArgumentNames));
    }


    @Test
    public void setDefaultArgumentValues_requestArgumentNamesAndValuesOfDifferentLengths_Exception() {
        Object[] requestArgumentValues = new Object[]{123, ""};
        List<String> requestArgumentNames = ImmutableList.of("", "", "");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                RequestArgumentsNamed.setDefaultArgumentValues(
                        this.getMethodArguments(),
                        requestArgumentValues,
                        requestArgumentNames));
    }

    
}