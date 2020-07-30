package com.atexpose.api;

import com.atexpose.api.datatypes.DataTypeEnum;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MethodObject_ConstructorArgumentValidationTest {

    @Test
    public void nullObject_ExceptionWithStringTheObject() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                MethodObject.builder()
                        .theObject(null)
                        .method(this.getClass().getDeclaredMethods()[0])
                        .description("")
                        .noOfRequiredArguments(0)
                        .arguments(Collections.emptyList())
                        .accessLevel(1)
                        .labels(Collections.emptyList())
                        .returnDataType(DataTypeEnum.INT.getInstance())
                        .build()
        ).withMessageContaining("theObject");
    }


    @Test
    public void nullMethod_ExceptionWithStringMethod() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                MethodObject.builder()
                        .theObject(this)
                        .method(null)
                        .description("")
                        .noOfRequiredArguments(0)
                        .arguments(Collections.emptyList())
                        .accessLevel(1)
                        .labels(Collections.emptyList())
                        .returnDataType(DataTypeEnum.INT.getInstance())
                        .build()
        ).withMessageContaining("method");
    }


    @Test
    public void nullDescription_ExceptionWithStringDescription() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                MethodObject.builder()
                        .theObject(this)
                        .method(this.getClass().getDeclaredMethods()[0])
                        .description(null)
                        .noOfRequiredArguments(0)
                        .arguments(Collections.emptyList())
                        .accessLevel(1)
                        .labels(Collections.emptyList())
                        .returnDataType(DataTypeEnum.INT.getInstance())
                        .build()
        ).withMessageContaining("description");
    }


    @Test
    public void nullReturnDataType_ExceptionWithStringReturnDataType() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                MethodObject.builder()
                        .theObject(this)
                        .method(this.getClass().getDeclaredMethods()[0])
                        .description("")
                        .noOfRequiredArguments(0)
                        .arguments(Collections.emptyList())
                        .accessLevel(1)
                        .labels(Collections.emptyList())
                        .returnDataType(null)
                        .build()
        ).withMessageContaining("returnDataType");
    }


    @Test
    public void noOfRequiredArguments_LargerThan_ArgumentListSize__ExceptionWithStringReturnDataType() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                MethodObject.builder()
                        .theObject(this)
                        .method(this.getClass().getDeclaredMethods()[0])
                        .description("")
                        .noOfRequiredArguments(2)
                        .arguments(Collections.emptyList())
                        .accessLevel(1)
                        .labels(Collections.emptyList())
                        .returnDataType(DataTypeEnum.INT.getInstance())
                        .build()
        ).withMessageContaining("Number of required arguments is higher than the actual number of arguments");
    }
}
