package com.atexpose.dispatcher.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.State;

/**
 * This class interprets requests in the TextParser format.
 * Example input 'setIt "44"'
 * Example output 'It set to 44'
 *
 * @author Schinzel
 */
public class TextParser extends AbstractParser {
    private static final Pattern REQUEST_PATTERN = Pattern.compile("(\\S+)( *)?([\\S\\s]*)");


    @Override
    public AbstractParser getClone() {
        return new TextParser();
    }


    // ------------------------------------
    // - PARSING PART
    // ------------------------------------
    @Override
    public void parseRequest(String request) {
        Matcher m = REQUEST_PATTERN.matcher(request);
        if (!m.find()) {
            throw new RuntimeError("Request '" + request + "' is not formed correctly.");
        }
        String methodName = m.group(1);
        String[] argumentValues = StringSplitter.splitOnComma_DoubleQuoteQualifier(m.group(3));
        this.setMethodRequest(methodName, argumentValues);
    }
    // ------------------------------------
    // - STATUS
    // ------------------------------------


    @Override
    public State getState() {
        return State.getBuilder().build();
    }


    @Override
    public boolean toRedirectToHttps() {
        return false;
    }


    @Override
    public String getUrlWithHttps() {
        throw new UnsupportedOperationException("Not implemented.");
    }


}
