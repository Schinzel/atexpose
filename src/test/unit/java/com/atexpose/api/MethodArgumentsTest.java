package com.atexpose.api;

import com.atexpose.api.datatypes.DataTypeEnum;
import com.google.common.collect.ImmutableList;
import io.schinzel.basicutils.state.State;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class MethodArgumentsTest {

    private MethodArguments getThreeArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataTypeEnum.STRING.getDataType())
                .defaultValue("my_default_value")
                .build();
        Argument argument2 = Argument.builder()
                .name("arg2")
                .dataType(DataTypeEnum.INT.getDataType())
                .defaultValue("1234")
                .build();
        Argument argument3 = Argument.builder()
                .name("arg3")
                .dataType(DataTypeEnum.BOOLEAN.getDataType())
                .defaultValue("true")
                .build();
        ImmutableList<Argument> arguments = new ImmutableList.Builder<Argument>()
                .add(argument1)
                .add(argument2)
                .add(argument3)
                .build();
        return MethodArguments.create(arguments);
    }


    @Test
    public void size_NullArgumentList_0() {
        int size = MethodArguments.create(null).size();
        assertThat(size).isZero();
    }


    @Test
    public void size_EmptyArgumentList_0() {
        int size = MethodArguments.create(Collections.emptyList()).size();
        assertThat(size).isZero();
    }


    @Test
    public void size_3ArgumentList_3() {
        int size = this.getThreeArguments().size();
        assertThat(size).isEqualTo(3);
    }


    @Test
    public void getCopyOfArgumentDefaultValues_3Arguments_DefaultValuesInCorrectDataTypes() {
        Object[] copyOfArgumentDefaultValues = this.getThreeArguments().getCopyOfArgumentDefaultValues();
        assertThat(copyOfArgumentDefaultValues)
                .containsExactly("my_default_value", 1234, true);
    }


    @Test
    public void getState_3Arguments_Arguments() {
        State state = this.getThreeArguments().getState();
        assertThat(state.getString()).contains("Arguments");
    }


}