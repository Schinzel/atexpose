package com.atexpose.dispatcher.invocation;

import com.atexpose.api.MethodArguments;
import com.atexpose.api.MethodObject;
import com.atexpose.errors.ExposedInvocationException;
import com.atexpose.errors.IExceptionProperties;
import com.atexpose.errors.RuntimeError;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Purpose of this class is ...
 * <p>
 * Created by Schinzel on 2017-12-10
 */

@Accessors(prefix = "m")
public class Invocation {
    @Getter private final Object mResponse;


    @Builder
    Invocation(MethodObject methodObject, List<String> requestArgumentValuesAsStrings, List<String> requestArgumentNames) throws ExposedInvocationException {
        MethodArguments methodArguments = methodObject.getMethodArguments();
        validateArgumentCount(requestArgumentValuesAsStrings, methodObject.getNoOfRequiredArguments(), methodArguments.size());
        Object[] requestArgumentValues = Checker.isEmpty(requestArgumentNames)
                ? methodArguments.cast(requestArgumentValuesAsStrings)
                : methodArguments.cast(requestArgumentValuesAsStrings, requestArgumentNames);
        int[] argumentPositions = Checker.isEmpty(requestArgumentNames)
                ? methodArguments.getArgumentPositions(requestArgumentNames)
                : IntStream.range(0, requestArgumentValues.length).toArray();
        Object[] argumentValuesAsObjects = R2.builder()
                .defaultArgumentValues(methodArguments.getCopyOfArgumentDefaultValues())
                .requestArgumentValues(requestArgumentValues)
                .argumentPositions(argumentPositions)
                .build()
                .getMArgumentValuesAsObjects();
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


    private static void validateArgumentCount(List<String> arguments, int minNumOfArgs, int maxNumOfArgs) {
        int noOfArgumentsInCall = Checker.isEmpty(arguments) ? 0 : arguments.size();
        boolean tooFewArguments = noOfArgumentsInCall < minNumOfArgs;
        boolean tooManyArguments = noOfArgumentsInCall > maxNumOfArgs;
        Thrower.throwIfTrue(tooFewArguments || tooManyArguments)
                .message("Incorrect number of arguments. Was " + noOfArgumentsInCall + ". Min is " + minNumOfArgs + " and max is " + maxNumOfArgs + ".");
    }
}
