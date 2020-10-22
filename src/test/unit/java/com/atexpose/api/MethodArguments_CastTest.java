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
public class MethodArguments_CastTest {

    private MethodArguments getThreeArguments() {
        Argument argument1 = Argument.builder()
                .name("arg1")
                .dataType(DataTypeEnum.STRING.getDataType())
                .defaultValue("my_default_value")
                .build();
        Argument argument2 = Argument.builder()
                .name("arg2")
                .dataType(DataTypeEnum.INT.getDataType())
                .defaultValue("12345")
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
    public void cast_NullValues_Exception() {
        List<String> argumentNames = Collections.emptyList();
        MethodArguments methodArguments = this.getThreeArguments();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> methodArguments.cast(null, argumentNames));
    }


    @Test
    public void cast_NullNames_Exception() {
        List<String> argumentValues = Collections.emptyList();
        MethodArguments methodArguments = this.getThreeArguments();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> methodArguments.cast(argumentValues, null));
    }


    @Test
    public void cast_ValuesAndNamesOfDifferentSizes_Exception() {
        List<String> argumentValues = ImmutableList.of("");
        List<String> argumentNames = ImmutableList.of("", "");
        MethodArguments methodArguments = this.getThreeArguments();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> methodArguments.cast(argumentValues, argumentNames));
    }


    @Test
    public void cast_EmptyNamesAndValues_EmptyObjectArray() {
        List<String> argumentValues = Collections.emptyList();
        List<String> argumentNames = Collections.emptyList();
        Object[] result = this.getThreeArguments().cast(argumentValues, argumentNames);
        assertThat(result).isEmpty();
    }


    @Test
    public void cast_ArgumentWithNames_ArgumentsInCorrectDataTypes() {
        List<String> argumentValues = ImmutableList.of("true", "1234");
        List<String> argumentNames = ImmutableList.of("arg3", "arg2");
        Object[] result = this.getThreeArguments().cast(argumentValues, argumentNames);
        assertThat(result).containsExactly(true, 1234);
    }


    @Test
    public void cast_ArgumentWithoutNames_ArgumentsInCorrectDataTypes() {
        List<String> argumentValues = ImmutableList.of("my_string", "1234");
        Object[] result = this.getThreeArguments().cast(argumentValues);
        assertThat(result).containsExactly("my_string", 1234);
    }

}
