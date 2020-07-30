package com.atexpose.api;

import com.atexpose.api.datatypes.DataTypeEnum;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-16
 */
public class MethodArguments_GetArgumentPositionsTest {

    private MethodArguments getThreeArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .alias("arg1_alias")
                .dataType(DataTypeEnum.STRING)
                .defaultValue("my_default_value")
                .build();
        Argument argument2 = Argument.builder()
                .name("arg2")
                .alias("arg2_alias")
                .dataType(DataTypeEnum.INT)
                .defaultValue("1234")
                .build();
        Argument argument3 = Argument.builder()
                .name("arg3")
                .alias("arg3_alias")
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
    public void getArgumentPositions__3Arguments_arg3Alias_arg2Alias_arg1Alias__Array_2_1_0() {
        List<String> list = ImmutableList.of("arg3_alias", "arg2_alias", "arg1_alias");
        int[] argumentPositions = this.getThreeArguments().getArgumentPositions(list);
        assertThat(argumentPositions).containsExactly(2, 1, 0);
    }


    @Test
    public void getCopyOfArgumentDefaultValues_NullConstructorArgument_EmptyArray() {
        Object[] copyOfArgumentDefaultValues = MethodArguments.create(null).getCopyOfArgumentDefaultValues();
        assertThat(copyOfArgumentDefaultValues).isEmpty();

    }

}
