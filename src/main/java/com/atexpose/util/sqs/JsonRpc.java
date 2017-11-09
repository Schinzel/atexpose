package com.atexpose.util.sqs;

import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
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
     * @return A JSON RPC. Example: {"method": "doSomething", "params": {"key1": "val1", "key2": "val2"}}
     */
    public String toString() {
        Thrower.throwIfVarEmpty(mMethodName, "MethodName");
        String arguments = getArguments(mArguments);
        return Str.create()
                .a("{")
                .aq("method", '"')
                .a(": ")
                .aq(mMethodName, '"')
                .ifTrue(Checker.isNotEmpty(mArguments))
                .a(", ")
                .a(arguments)
                .endIf()
                .a("}")
                .toString();
    }


    /**
     * @param arguments Arguments to convert to string
     * @return The argument map as a string. Example: "params": {"key1": "val1", "key2": "val2"}
     */
    static String getArguments(Map<String, String> arguments) {
        Thrower.throwIfVarNull(arguments, "arguments");
        //Get arguments as string: "key1": "val1", "key2": "val2"
        String argumentAsString = arguments.entrySet()
                .stream()
                .map(entry -> '"' + entry.getKey() + "\": \"" + entry.getValue() + '"')
                .collect(Collectors.joining(", "));
        return Str.create()
                .aq("params", '"')
                .a(": ")
                .aq(argumentAsString, "{", "}")
                .toString();
    }


}
