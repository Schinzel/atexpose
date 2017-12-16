package com.atexpose.dispatcher.invocation;

import com.atexpose.api.MethodObject;
import com.atexpose.errors.ExposedInvocationException;
import com.atexpose.errors.IExceptionProperties;
import com.atexpose.errors.RuntimeError;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-10
 */

@Accessors(prefix = "m")
public class Invocation {
    @Getter private final Object mResponse;


    @Builder
    Invocation(MethodObject methodObject, Object[] argumentValuesAsObjects) throws ExposedInvocationException {
        mResponse = Invocation.invoke(methodObject.getMethod(), methodObject.getObject(), argumentValuesAsObjects);
    }


    static Object invoke(Method method, Object object, Object[] argumentValuesAsObjects) throws ExposedInvocationException {
        try {
            return method.invoke(object, argumentValuesAsObjects);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            StackTraceElement ste = cause.getStackTrace()[0];
            Map<String, String> properties = ImmutableMap.<String, String>builder()
                    .put("ErrorMessage", cause.getMessage())
                    .put("Method", ste.getMethodName())
                    .put("Class", ste.getClassName())
                    .put("LineNumber", String.valueOf(ste.getLineNumber()))
                    .build();
            if (ite.getCause() instanceof IExceptionProperties) {
                properties = ImmutableMap.<String, String>builder()
                        .putAll(properties)
                        .putAll(((IExceptionProperties) cause).getProperties())
                        .build();
            }
            throw new ExposedInvocationException(properties);
        } catch (IllegalAccessException iae) {
            throw new RuntimeError("Access error " + iae.toString());
        }
    }



}
