package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.substringer.SubStringer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * The purpose of this class is to parse a request in the http format. Both post
 * and get is supported. It the whole request including the header.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class URLParser extends AbstractParser {
    /**
     * This marker is the suffix to all commands. E.g.
     * http://127.0.0.1:5555/call/setName?name=anyname If this suffix is not
     * present a file i assumed http;//127.0.0.1:5555/myfile.jpg
     */
    private static final String COMMAND_REQUEST_MARKER = PropertiesDispatcher.COMMAND_REQUEST_MARKER;
    @Getter(AccessLevel.PROTECTED) protected HttpRequest mHttpRequest;
    @Getter(AccessLevel.PROTECTED) protected CallType mCallType;


    enum CallType {
        COMMAND, FILE, UNKNOWN
    }
    // ---------------------------------
    // - CONSTRUCTORS  -
    // ---------------------------------


    @Override
    public AbstractParser getClone() {
        return new URLParser();
    }


    // ---------------------------------
    // - OPERATIONS  -
    // ---------------------------------
    @Override
    public void parseRequest(String request) {
        mCallType = CallType.UNKNOWN;
        mHttpRequest = new HttpRequest(request);
        String url = mHttpRequest.getURL();
        //If is command call
        if (url.contains(COMMAND_REQUEST_MARKER)) {
            mCallType = CallType.COMMAND;
            Map<String, String> map = mHttpRequest.getVariablesAsMap();
            String[] argValues = new String[map.size()];
            String[] argNames = new String[map.size()];
            int index = 0;
            //Go through all arguments
            for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                //Put the name in array
                argNames[index] = mapEntry.getKey();
                //Put the value in array
                argValues[index] = mapEntry.getValue();
                //Decode the value
                argValues[index] = UrlCoding.decodeURIComponent(argValues[index]);
                index++;
            }
            String methodName = getMethodName(mHttpRequest.getPath());
            this.setMethodRequest(methodName, argValues, argNames);
            // }
        }//else, is request for file
        else {
            mCallType = CallType.FILE;
            //Get the part of the url before ?, if any
            url = SubStringer.create(url).endDelimiter("?").toString();
            this.setFileRequest(url);
        }
    }
    // ---------------------------------
    // - PRIVATE STATIC  -
    // ---------------------------------


    /**
     * @param path The path part of a url
     * @return The method name invoked
     */
    private static String getMethodName(String path) {
        String returnString = EmptyObjects.EMPTY_STRING;
        if (!Checker.isEmpty(path)) {
            returnString = path.substring(URLParser.COMMAND_REQUEST_MARKER.length());
        }
        return returnString;
    }
    // ---------------------------------
    // - STATUS  -
    // ---------------------------------


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .build();
    }


}
