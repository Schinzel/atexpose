package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.FunnyChars;
import io.schinzel.basicutils.state.State;
import org.json.JSONException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class JsonRpcParserTest {


    @Test
    public void getClone_NewlyCreatedInstance_AnotherInstance() {
        JsonRpcParser jsonRpcParser = new JsonRpcParser();
        IParser clone = jsonRpcParser.getClone();
        assertThat(clone).isNotEqualTo(jsonRpcParser);
    }


    @Test
    public void getClone_NewlyCreatedInstance_IsJsonRpcParser() {
        IParser clone = new JsonRpcParser().getClone();
        assertThat(clone).isInstanceOf(JsonRpcParser.class);
    }


    @Test
    public void getState_NewlyCreatedInstance_IsNonEmptyState() {
        State state = new JsonRpcParser().getState();
        assertThat(state.getString().length()).isGreaterThan(0);
    }


    @Test
    public void getRequest_RequestWithMethodNoParams_ShouldFindMethod() {
        String requestAsString = "{\"method\": \"doSomething\"}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getMethodName()).isEqualTo("doSomething");
    }


    @Test
    public void getRequest_RequestWithMethodWithParams_ShouldFindMethod() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": 23, \"para2\": 42}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getMethodName()).isEqualTo("doSomething");
    }


    @Test
    public void getRequest_EmptyString_ThrowException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new JsonRpcParser().getRequest("")
        );
    }


    @Test
    public void getRequest_NullString_ThrowException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new JsonRpcParser().getRequest(null)
        );
    }


    @Test
    public void getRequest_MethodPropertyMissing_ThrowException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new JsonRpcParser().getRequest("{\"params\": {\"para1\": 23, \"para2\": 42}}")
        );
    }


    @Test
    public void getRequest_InCorrectJsonClosingCurlyMissing_ThrowException() {
        assertThatExceptionOfType(JSONException.class).isThrownBy(() ->
                new JsonRpcParser().getRequest("{\"method\": \"doSomething\"")
        );
    }


    @Test
    public void getRequest_AccordingToJsonRpc2Spec_ShouldWork() {
        Request jsonRpc2Request = new JsonRpcParser().getRequest("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}, \"id\": 3}");
        Request bareEssentialsRequest = new JsonRpcParser().getRequest("{\"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}}");
        assertThat(jsonRpc2Request.toString()).isEqualTo(bareEssentialsRequest.toString());
    }


    @Test
    public void getRequest_RequestWithMethodOneParam_OneParamWithCorrectNameAndValue() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": 23}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.toString())
                .isEqualTo("Request(mMethodName=doSomething, mArgumentNames=[para1], mArgumentValues=[23], mFileName=, mFileRequest=false)");
    }


    @Test
    public void getRequest_RequestWithMethodTwoParams_TwoParamsWithCorrectNameAndValue() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": 23, \"para2\": 42}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.toString())
                .isEqualTo("Request(mMethodName=doSomething, mArgumentNames=[para1, para2], mArgumentValues=[23, 42], mFileName=, mFileRequest=false)");
    }


    @Test
    public void getRequest_ArgOfTypeString_StringVersionOfArg() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": \"my_para\"}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getArgumentValues().get(0)).isEqualTo("my_para");
    }


    @Test
    public void getRequest_ArgOfTypeInteger_StringVersionOfArg() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": 12345}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getArgumentValues().get(0)).isEqualTo("12345");
    }


    @Test
    public void getRequest_ArgOfTypeFloat_StringVersionOfArg() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": 123.45}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getArgumentValues().get(0)).isEqualTo("123.45");
    }


    @Test
    public void getRequest_ArgOfTypeBoolean_StringVersionOfArg() {
        String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": true}}";
        Request request = new JsonRpcParser().getRequest(requestAsString);
        assertThat(request.getArgumentValues().get(0)).isEqualTo("true");
    }


    @Test
    public void getRequest_ArgFunnyChars_StringVersionOfArg() {
        for (FunnyChars funnyChars : FunnyChars.values()) {
            String input = funnyChars.getString();
            String requestAsString = "{\"method\": \"doSomething\", \"params\": {\"para1\": \"" + input + "\"}}";
            Request request = new JsonRpcParser().getRequest(requestAsString);
            assertThat(request.getArgumentValues().get(0)).isEqualTo(input);
        }
    }


    @Test
    public void getRequest_UnnamedJsonRpcCall_ThrowException() {
        String requestAsString = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [23, 42], \"id\": 2}";
        assertThatExceptionOfType(JSONException.class).isThrownBy(() ->
                new JsonRpcParser().getRequest(requestAsString)
        );
    }

}
