package com.atexpose.api;

import com.atexpose.errors.ExposedInvocationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class MethodObjectTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void invoke_InvokeMethodThrowsException_ThrowsException() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() ->
                MethodObject.invoke(method, object, argumentValuesAsObjects)
        );
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMethodName() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("method:doAnOperation");
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionLineNumber() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("line_number:");
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMessage() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("error_message:Something went wrong!");
    }

}