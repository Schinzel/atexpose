package io.schinzel.samples.exception;

import com.atexpose.errors.IExceptionProperties;

import java.util.Map;

public class MyException extends RuntimeException implements IExceptionProperties {

    private final Map<String, String> mProperties;

    public MyException(String message, Map<String, String> properties) {
        super(message);
        mProperties = properties;
    }


    public Map<String, String> getProperties() {
        return mProperties;
    }


}
