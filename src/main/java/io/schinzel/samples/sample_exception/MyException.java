package io.schinzel.samples.sample_exception;

import com.atexpose.errors.IProperties;

import java.util.Map;

/**
 * Created by schinzel on 2017-07-11.
 */
public class MyException extends RuntimeException implements IProperties {

    private final Map<String, String> mProperties;


    public MyException(String message, Map<String, String> properties) {
        super(message);
        mProperties = properties;
    }


    public Map<String, String> getProperties() {
        return mProperties;
    }


}
