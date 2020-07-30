package com.atexpose.api;

import com.atexpose.api.datatypes.DataTypeEnum;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-16
 */
public class MethodArguments_GetArgumentTest {
    private MethodArguments getThreeArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataTypeEnum.STRING)
                .defaultValue("my_default_value")
                .build();
        Argument argument2 = Argument.builder()
                .name("arg2")
                .dataType(DataTypeEnum.INT)
                .defaultValue("1234")
                .build();
        Argument argument3 = Argument.builder()
                .name("arg3")
                .dataType(DataTypeEnum.BOOLEAN)
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
    public void getArgument_NonExistingArgumentName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgument("non_existing_name")
        ).withMessageContaining("No argument named ");
    }


    @Test
    public void getArgument_NullArgumentName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgument(null)
        );
    }


    @Test
    public void getArgument_EmptyArgumentName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgument("")
        );
    }


    @Test
    public void getArgument_ExistingArgumentName_ArgumentWithRequestedName() {
        Argument argument = this.getThreeArguments().getArgument("arg2");
        assertThat(argument.getKey()).isEqualToIgnoringCase("arg2");
    }


    @Test
    public void getArgument_minus1_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgument(-1)
        ).withMessageContaining("Requested argument position '-1' is out of bounds.");
    }


    @Test
    public void getArgument__3Arguments_RequestedPosition10__Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgument(10)
        ).withMessageContaining("Requested argument position '10' is out of bounds.");
    }


    @Test
    public void getArgument__3Arguments_RequestedPosition2__ThirdArgument() {
        Argument argument = this.getThreeArguments().getArgument(2);
        assertThat(argument.getKey()).isEqualToIgnoringCase("arg3");
    }

}
