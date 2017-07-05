package com.atexpose.dispatcher.parser;

import com.google.common.collect.Lists;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.State;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to parse requests formatted as JSON.
 * The format is a subset of JSON-RPC
 * http://www.jsonrpc.org/specification
 * <p>
 * test cases
 * - att ett full json rpc 2.0 call funar
 * - unnamed params
 * <p>
 * - alla möjliga data typer for params
 * <p>
 * <p>
 * Created by schinzel on 2017-07-04.
 */
public class JsonRpcParser implements IParser {

    @Override
    public Request getRequest(String incomingRequest) {
        Thrower.throwIfVarEmpty(incomingRequest, "incomingRequest");
        JSONObject json = new JSONObject(incomingRequest);
        Thrower.throwIfFalse(json.has("method"), "Incorrect JSON-RPC format. Method missing. Request: '" + json.toString() + "'");
        if (json.has("params")) {
            JSONObject params = json.getJSONObject("params");
            List<String> argNames = Lists.newArrayList(params.keys());
            List<String> argValues = argNames.stream()
                    .map(v -> String.valueOf(params.get(v)))
                    .collect(Collectors.toList());
            return Request.builder()
                    .methodName(json.getString("method"))
                    .argumentNames(argNames)
                    .argumentValues(argValues)
                    .build();
        } else {
            return Request.builder()
                    .methodName(json.getString("method"))
                    .build();
        }
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


    public static void main(String[] args) {
        String str = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}, \"id\": 3}";
        JSONObject json = new JSONObject(str);
        System.out.println(json.toString(3));
        JSONObject params = json.getJSONObject("params");
        Iterator<String> keys = params.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(" " + key + " " + String.valueOf(params.get(key)));
        }
        int apa = 22;

    }
}
