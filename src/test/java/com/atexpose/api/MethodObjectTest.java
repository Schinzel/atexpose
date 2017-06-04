package com.atexpose.api;

import io.schinzel.basicutils.substring.SubString;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MethodObjectTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void invoke_InvokeMethodThrowsException_ThrowsException() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        try {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
            fail("Expected exception did not occur.");
        } catch (RuntimeException e) {
        }
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMethodName() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        try {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
            fail("Expected exception did not occur.");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            String methodName = SubString.create(message).startDelimiter("Method: ").endDelimiter("\n").getString();
            assertEquals("doAnOperation", methodName);
        }
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionLineNumber() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        try {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
            fail("Expected exception did not occur.");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            String lineNumber = SubString.create(message).startDelimiter("Line num: ").endDelimiter("\n").getString();
            Assert.assertTrue("Line number should be numeric", StringUtils.isNumeric(lineNumber));
        }
    }


    @Test
    public void invoke_InvokeMethodThrowsException_ExceptionMessage() throws Exception {
        Method method = ExposedClassUtil.class.getMethod("doIt");
        Object object = new ExposedClassUtil();
        Object[] argumentValuesAsObjects = new Object[]{};
        try {
            MethodObject.invoke(method, object, argumentValuesAsObjects);
            fail("Expected exception did not occur.");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            String errorMessage = SubString.create(message).startDelimiter("Message: ").endDelimiter("\n").getString();
            assertEquals("Something went wrong!", errorMessage);
        }
    }

}