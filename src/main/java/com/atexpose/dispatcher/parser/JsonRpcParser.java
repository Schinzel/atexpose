package com.atexpose.dispatcher.parser;

import com.google.common.collect.Lists;
import io.schinzel.basicutils.thrower.Thrower;
import io.schinzel.basicutils.state.State;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to parse requests formatted as JSON.
 * <p>
 * Samples:
 * {"method": "doSomething"}
 * {"method": "doSomething", "params": {"para1": 23, "para2": 42}}
 * {"method": "doSomething", "params": {"para1": "my_string", "para2": 12.45, "para3: true}}
 * <p>
 * The format is a subset of JSON-RPC 2.0 http://www.jsonrpc.org/specification
 * Works with only named parameters.
 * Does not check for keys "jsonrpc" nor "id".
 * <p>
 * Created by schinzel on 2017-07-04.
 */
public class JsonRpcParser implements IParser {

    @Override
    public Request getRequest(String incomingRequest) {
        Thrower.throwIfVarEmpty(incomingRequest, "incomingRequest");
        JSONObject json = new JSONObject(incomingRequest);
        Thrower.throwIfFalse(json.has("method"), "Incorrect JSON-RPC format. Method missing. Request: '" + json.toString() + "'");
        List<String> argNames = Collections.emptyList();
        List<String> argValues = Collections.emptyList();
        if (json.has("params")) {
            JSONObject params = json.getJSONObject("params");
            argNames = Lists.newArrayList(params.keys());
            argValues = argNames.stream()
                    .map(v -> String.valueOf(params.get(v)))
                    .collect(Collectors.toList());
        }
        return Request.builder()
                .methodName(json.getString("method"))
                .argumentNames(argNames)
                .argumentValues(argValues)
                .build();
    }


    @Override
    public IParser getClone() {
        return new JsonRpcParser();
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .build();
    }
}
