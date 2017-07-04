package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.State;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * The purpose of this class is to parse requests frommated as JSON.
 * test cases
 * - incorrect json
 * - inga params
 * - tom params
 * - unnamed params
 * <p>
 * <p>
 * Created by schinzel on 2017-07-04.
 */
public class JsonRpcParser implements IParser {
    @Override
    public Request getRequest(String incomingRequest) {
        JSONObject json = new JSONObject(incomingRequest);
        Thrower.throwIfFalse(json.has("method"), "Incorrect JSON-RPC format. Method missing. Request: '" + json.toString() + "'");
        //String methodName = json.getString("method");
        JSONObject params = json.getJSONObject("params");
        Iterator<String> paramNames = params.keys();
        String[] argValues = new String[params.length()];
        String[] argNames = new String[params.length()];
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            //argValues = paramName;
            return null;
        }
        return null;
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
