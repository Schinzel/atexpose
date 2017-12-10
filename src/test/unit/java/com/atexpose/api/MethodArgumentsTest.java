package com.atexpose.api;

import com.atexpose.api.datatypes.DataType;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class MethodArgumentsTest {

    private MethodArguments getThreeArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataType.STRING)
                .defaultValue("my_default_value")
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
        assertThat(size).isEqualTo(0);
    }


    @Test
    public void size_EmptyArgumentList_0() {
        int size = MethodArguments.create(Collections.emptyList()).size();
        assertThat(size).isEqualTo(0);
    }


    @Test
    public void size_3ArgumentList_3() {
        int size = this.getThreeArguments().size();
        assertThat(size).isEqualTo(3);
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


    @Test
    public void getArgumentPosition_NullName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgumentPosition(null)
        ).withMessageContaining("Argument 'argumentName' cannot be empty");
    }


    @Test
    public void getArgumentPosition_EmptyName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgumentPosition("")
        ).withMessageContaining("Argument 'argumentName' cannot be empty");

    }


    @Test
    public void getArgumentPosition_NonExistingName_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgumentPosition("no_such_name")
        ).withMessageContaining("No argument named 'no_such_name'");

    }


    @Test
    public void getArgumentPosition__3Arguments_arg1__0() {
        int argumentPosition = this.getThreeArguments().getArgumentPosition("arg1");
        assertThat(argumentPosition).isEqualTo(0);
    }


    @Test
    public void getArgumentPositions_Null_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgumentPositions(null)
        ).withMessageContaining("Argument 'argumentNames' cannot be null");
    }


    @Test
    public void getArgumentPositions_3Arguments_NonExistingName_Exception() {
        List<String> list = ImmutableList.of("arg1", "no_such_name");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getThreeArguments().getArgumentPositions(list)
        ).withMessageContaining("No argument named 'no_such_name'");
    }


    @Test
    public void getArgumentPositions_EmptyList_EmptyArray() {
        int[] argumentPositions = this.getThreeArguments().getArgumentPositions(Collections.emptyList());
        assertThat(argumentPositions).isEmpty();
    }


    @Test
    public void getArgumentPositions__3Arguments_arg1__Array_0() {
        List<String> list = ImmutableList.of("arg1");
        int[] argumentPositions = this.getThreeArguments().getArgumentPositions(list);
        assertThat(argumentPositions).containsExactly(0);
    }


    @Test
    public void getArgumentPositions__3Arguments_arg3_arg2_arg1__Array_2_1_0() {
        List<String> list = ImmutableList.of("arg3", "arg2", "arg1");
        int[] argumentPositions = this.getThreeArguments().getArgumentPositions(list);
        assertThat(argumentPositions).containsExactly(2, 1, 0);
    }


    @Test
    public void getCopyOfArgumentDefaultValues_NullConstructorArgument_EmptyArray() {
        Object[] copyOfArgumentDefaultValues = MethodArguments.create(null).getCopyOfArgumentDefaultValues();
        assertThat(copyOfArgumentDefaultValues).isEmpty();

    }


    @Test
    public void getCopyOfArgumentDefaultValues_3Arguments_() {
        Object[] copyOfArgumentDefaultValues = this.getThreeArguments().getCopyOfArgumentDefaultValues();
        assertThat(copyOfArgumentDefaultValues)
                .containsExactly("my_default_value", 1234, true);
    }
}