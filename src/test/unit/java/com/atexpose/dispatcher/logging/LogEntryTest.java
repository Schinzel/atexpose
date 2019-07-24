package com.atexpose.dispatcher.logging;

import com.google.common.collect.ImmutableList;
import io.schinzel.crypto.cipher.ICipher;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogEntryTest {

    private LogEntry.LogEntryBuilder getLogEntryBuilder() {
        return LogEntry.builder()
                .isError(false)
                .timeOfIncomingRequest(Instant.now())
                .requestString("decodedIncomingRequest")
                .response("wrappedResponse")
                .threadNumber(17)
                .requestReadTime(123L)
                .execTime(456L)
                .responseWriteTime(789L)
                .senderInfo("my_sender_info")
                .argNames(ImmutableList.of("arg_name_1", "arg_name_2"))
                .argValues(ImmutableList.of("arg_value_1", "arg_value_2"))
                .isFileRequest(false)
                .fileName("my_file_name")
                .methodName("my_method_name");

    }


    @Test
    public void getLogData_CryptoNull_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getLogEntryBuilder().build().getLogData(null)
        );
    }


    @Test
    public void getLogData_Crypto_ArgumentValuesShouldBeEncrypted() {
        ICipher cipher = mock(ICipher.class);
        when(cipher.encrypt(anyString())).then(i -> i.getArgument(0) + "_encrypted");
        LogEntry logEntry = this.getLogEntryBuilder()
                .argValues(ImmutableList.of("arg_value_1", "arg_value_2"))
                .build();
        Map<String, String> logData = logEntry.getLogData(cipher);
        String arguments = logData.get("arguments");
        assertThat(arguments).isEqualTo("arg_name_1='arg_value_1_encrypted', arg_name_2='arg_value_2_encrypted'");
    }


    @Test
    public void getLogData_FileRequest_ContainsFileNameButNotMethodNameNorArguments() {
        ICipher cipher = mock(ICipher.class);
        when(cipher.encrypt(anyString())).then(returnsFirstArg());
        Map<String, String> logData = this.getLogEntryBuilder()
                .isFileRequest(true)
                .fileName("my_file_name")
                .build()
                .getLogData(cipher);
        assertThat(logData).containsKey(LogEntry.KEY_FILENAME)
                .doesNotContainKey(LogEntry.KEY_METHOD_NAME)
                .doesNotContainKey(LogEntry.KEY_ARGUMENTS);
    }


    @Test
    public void getLogData_MethodRequest_ContainsMethodNameAndArgumentsButNotFilename() {
        ICipher cipher = mock(ICipher.class);
        when(cipher.encrypt(anyString())).then(returnsFirstArg());
        Map<String, String> logData = this.getLogEntryBuilder()
                .isFileRequest(false)
                .methodName("my_method")
                .argNames(ImmutableList.of("arg_name_1", "arg_name_2"))
                .argValues(ImmutableList.of("arg_value_1", "arg_value_2"))
                .build()
                .getLogData(cipher);
        assertThat(logData).doesNotContainKey(LogEntry.KEY_FILENAME)
                .containsKey(LogEntry.KEY_METHOD_NAME)
                .containsKey(LogEntry.KEY_ARGUMENTS);
    }


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