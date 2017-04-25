package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.substringer.SubStringer;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Map;

/**
 * The purpose of this class is to parse a request in the http format. Both post
 * and get is supported. It the whole request including the header.
 * <p>
 * Created by schinzel on 2017-04-25.
 */
public class UrlParser2 implements IParser {
    @Getter(AccessLevel.PROTECTED) protected URLParser.CallType mCallType;


    enum CallType {
        COMMAND, FILE, UNKNOWN
    }


    @Override
    public Request getRequest(String incomingRequest) {
        mCallType = URLParser.CallType.UNKNOWN;
        HttpRequest mHttpRequest = new HttpRequest(incomingRequest);
        String url = mHttpRequest.getURL();
        //If is command call
        if (url.contains(PropertiesDispatcher.COMMAND_REQUEST_MARKER)) {
            mCallType = URLParser.CallType.COMMAND;
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
            return Request.builder()
                    .methodName(methodName)
                    .argumentValues(argValues)
                    .argumentNames(argNames)
                    .fileRequest(false)
                    .build();
            // }
        }//else, is request for file
        else {
            mCallType = URLParser.CallType.FILE;
            //Get the part of the url before ?, if any
            url = SubStringer.create(url).endDelimiter("?").toString();
            return Request.builder()
                    .fileRequest(true)
                    .fileName(url)
                    .build();
        }
    }


    @Override
    public IParser getClone() {
        return new UrlParser2();
    }


    /**
     * @param path The path part of a url
     * @return The method name invoked
     */
    private static String getMethodName(String path) {
        String returnString = EmptyObjects.EMPTY_STRING;
        if (!Checker.isEmpty(path)) {
            returnString = path.substring(PropertiesDispatcher.COMMAND_REQUEST_MARKER.length());
        }
        return returnString;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .build();
    }
}
