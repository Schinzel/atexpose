package com.atexpose.dispatcher.invocation;

import com.atexpose.api.ExposedClassUtil;
import com.atexpose.errors.ExposedInvocationException;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by Schinzel on 2017-12-10
 */
public class InvocationTest {
    @Test
    public void invoke_InvokeMethodThrowsException_ThrowsException() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() ->
                Invocation.invoke(method, object, argumentValuesAsObjects)
        );
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMethodName() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            Invocation.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("Method:doAnOperation");
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionLineNumber() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            Invocation.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("LineNumber:");
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMessage() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        assertThatExceptionOfType(ExposedInvocationException.class).isThrownBy(() -> {
            Invocation.invoke(method, object, argumentValuesAsObjects);
        }).withMessageContaining("ErrorMessage:Something went wrong!");
    }

}