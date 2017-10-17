package com.atexpose.util.sqs;

import io.schinzel.basicutils.str.Str;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to compile JSON RPC messages. The format is JSON RPC 2.0.
 */
@Builder
@Accessors(prefix = "m")
public class JsonRpc {
    private final String mMethodName;
    @Singular
    private Map<String, String> mArguments;


    /**
     * @return A JSON RPC. Example: {"method": "doSomething", "params": {"para1": "val1", "para2": "val2"}}
     */
    public String toString() {
        //Convert arguments to: {"key1": "val1", "val1": "val2"}
        String arguments = mArguments.entrySet()
                .stream()
                .map(entry -> '"' + entry.getKey() + "\": \"" + entry.getValue() + '"')
                .collect(Collectors.joining(", "));
        return Str.create()
                .a("{")
                .aq("method", '"')
                .a(": ")
                .aq(mMethodName, '"')
                .a(", ")
                .aq("params", '"')
                .a(": ")
                .aq(arguments, "{", "}")
                .a("}")
                .toString();
    }


}
