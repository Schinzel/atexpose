package io.schinzel.samples.sample_exception;

import com.atexpose.Expose;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * A sample class.
 * <p>
 * Created by schinzel on 2017-07-11.
 */
public class MyClass {

    @Expose(
            description = "Method throws an exception with custom key-value properties. "
    )
    public static String myMethod() {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("my_first_custom_key", "1234")
                .put("my_second_custom_key", "abcd")
                .build();
        throw new MyException("my myssage", properties);
    }
}
