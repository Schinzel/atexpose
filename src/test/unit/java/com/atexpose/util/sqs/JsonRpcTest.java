package com.atexpose.util.sqs;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JsonRpcTest {

    @Test
    public void toString_NoArgument_CallWithMethodName() {
        String jsonRpc = JsonRpc.builder()
                .methodName("MyMethod")
                .build()
                .toString();
        assertThat(jsonRpc).isEqualTo("{\"method\": \"MyMethod\"}");
    }


    @Test
    public void toString_EmptyMethodName_Exception() {
        JsonRpc jsonRpc = JsonRpc.builder()
                .methodName("")
                .build();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> jsonRpc.toString());
    }


    @Test
    public void toString_NullMethodName_Exception() {
        JsonRpc jsonRpc = JsonRpc.builder()
                .methodName(null)
                .build();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> jsonRpc.toString());
    }


    @Test
    public void toString_WithArguments_CorrectRpcCall() {
        String jsonRpc = JsonRpc.builder()
                .methodName("MyMethod")
                .argument("key1", "val1")
                .argument("key2", "val2")
                .build()
                .toString();
        assertThat(jsonRpc)
                .isEqualTo("{\"method\": \"MyMethod\", \"params\": {\"key1\": \"val1\", \"key2\": \"val2\"}}");
    }


    @Test
    public void getArguments_Null_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> JsonRpc.getArguments(null));
    }


    @Test
    public void getArguments_EmptyMap_() {
        String arguments = JsonRpc.getArguments(Collections.emptyMap());
        assertThat(arguments)
                .isEqualTo("\"params\": {}");
    }


    @Test
    public void getArguments_NonEmptyMap_() {
        Map<String, String> map = ImmutableMap.<String, String>builder()
                .put("key1", "val1")
                .put("key2", "val2")
                .build();
        String arguments = JsonRpc.getArguments(map);
        assertThat(arguments)
                .isEqualTo("\"params\": {\"key1\": \"val1\", \"key2\": \"val2\"}");

    }
}