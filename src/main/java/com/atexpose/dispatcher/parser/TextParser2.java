package com.atexpose.dispatcher.parser;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.State;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class interprets requests in the TextParser format.
 * Example input 'setIt "44"'
 * Example output 'It set to 44'
 * <p>
 * Created by schinzel on 2017-04-25.
 */
public class TextParser2 implements IParser {
    private static final Pattern REQUEST_PATTERN = Pattern.compile("(\\S+)( *)?([\\S\\s]*)");


    @Override
    public Request getRequest(String incomingRequest) {
        Matcher m = REQUEST_PATTERN.matcher(incomingRequest);
        if (!m.find()) {
            throw new RuntimeError("Request '" + incomingRequest + "' is not formed correctly.");
        }
        String methodName = m.group(1);
        String[] argumentValues = StringSplitter.splitOnComma_DoubleQuoteQualifier(m.group(3));
        return Request.builder()
                .methodName(methodName)
                .argumentValues(argumentValues)
                .build();
    }


    @Override
    public IParser getClone() {
        return new TextParser2();
    }


    @Override
    public State getState() {
        return State.getBuilder().build();
    }
}
