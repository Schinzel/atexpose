package com.atexpose.dispatcher.logging;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class LogEntryTest {


    @Test
    public void argumentsToString_EmptyNamesAndValues_HyphenMinus() {
        String argString = LogEntry.argumentsToString(Collections.emptyList(), Collections.emptyList());
        assertThat(argString).isEqualTo("-");
    }


    @Test
    public void argumentsToString_NotEmptyNamesAndEmptyValues_Exception() {
        List<String> argNames = ImmutableList.of("name_1");
        List<String> argValues = Collections.emptyList();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                LogEntry.argumentsToString(argNames, argValues)
        );
    }


    @Test
    public void argumentsToString_DifferentAmountsOfArgNamesAndValues_Exception() {
        List<String> argNames = ImmutableList.of("name_1");
        List<String> argValues = ImmutableList.of("val_1", "val_2");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                LogEntry.argumentsToString(argNames, argValues)
        );
    }


    @Test
    public void argumentsToString_NullArgNames_Exception() {
        List<String> argNames = null;
        List<String> argValues = ImmutableList.of("val_1", "val_2");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                LogEntry.argumentsToString(argNames, argValues)
        );
    }


    @Test
    public void argumentsToString_EmptyNamesAndOneValue_() {
        List<String> argNames = Collections.emptyList();
        List<String> argValues = ImmutableList.of("val_1");
        String argString = LogEntry.argumentsToString(argNames, argValues);
        assertThat(argString).isEqualTo("'val_1'");
    }


    @Test
    public void argumentsToString_EmptyNamesAndCoupleOfValues_() {
        List<String> argNames = Collections.emptyList();
        List<String> argValues = ImmutableList.of("val_1", "val_2");
        String argString = LogEntry.argumentsToString(argNames, argValues);
        assertThat(argString).isEqualTo("'val_1', 'val_2'");
    }


    @Test
    public void argumentsToString_OneOfNameAndValue_() {
        List<String> argNames = ImmutableList.of("name_1");
        List<String> argValues = ImmutableList.of("val_1");
        String argString = LogEntry.argumentsToString(argNames, argValues);
        assertThat(argString).isEqualTo("name_1='val_1'");
    }


    @Test
    public void argumentsToString_CoupleOfNamesAndValues_() {
        List<String> argNames = ImmutableList.of("name_1", "name_2");
        List<String> argValues = ImmutableList.of("val_1", "val_2");
        String argString = LogEntry.argumentsToString(argNames, argValues);
        assertThat(argString).isEqualTo("name_1='val_1', name_2='val_2'");
    }
}