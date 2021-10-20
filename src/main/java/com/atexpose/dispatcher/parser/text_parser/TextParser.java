package com.atexpose.dispatcher.parser.text_parser;

import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.State;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class interprets requests in the TextParser format.
 * Example input 'setIt "44"'
 * Example output 'It set to 44'
 * <p>
 * Created by schinzel on 2017-04-25.
 */
public class TextParser implements IParser {
    private static final Pattern REQUEST_PATTERN = Pattern.compile("(\\S+)( *)?([\\S\\s]*)");


    @Override
    public Request getRequest(String requestAsString) {
        Matcher m = REQUEST_PATTERN.matcher(requestAsString);
        if (!m.find()) {
            throw new RuntimeError("Request '" + requestAsString + "' is not formed correctly.");
        }
        String methodName = m.group(1);
        List<String> argumentValues = StringSplitter.splitOnComma_SingleQuoteQualifier(m.group(3));
        return Request.builder()
                .methodName(methodName)
                .argumentValues(argumentValues)
                .build();
    }


    @Override
    public IParser getClone() {
        return new TextParser();
    }


    @Override
    public State getState() {
        return State.getBuilder().build();
    }
}
