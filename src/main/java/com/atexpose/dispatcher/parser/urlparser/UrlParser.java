package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.substring.SubString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The purpose of this class is to parse a request in the http format. Both post
 * and get is supported. It the whole request including the header.
 * <p>
 * Created by Schinzel on 2017-04-25.
 */
@Accessors(prefix = "m")
public class UrlParser implements IParser {
    @Getter(AccessLevel.PROTECTED) protected CallType mCallType;
    @Getter(AccessLevel.PROTECTED) HttpRequest mHttpRequest;

    enum CallType {
        COMMAND, FILE, UNKNOWN
    }


    @Override
    public Request getRequest(String incomingRequest) {
        mCallType = CallType.UNKNOWN;
        mHttpRequest = new HttpRequest(incomingRequest);
        String url = mHttpRequest.getURL();
        //If is command call
        if (url.contains(PropertiesDispatcher.COMMAND_REQUEST_MARKER)) {
            mCallType = CallType.COMMAND;
            Map<String, String> map = mHttpRequest.getVariablesAsMap();
            List<String> argNames = new ArrayList<>();
            List<String> argValues = new ArrayList<>();
            //Go through all arguments
            for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                //Put the name in array
                argNames.add(mapEntry.getKey());
                //Decode and put the value in array
                argValues.add(UrlCoding.decodeURIComponent(mapEntry.getValue()));
            }
            String methodName = getMethodName(mHttpRequest.getPath());
            return Request.builder()
                    .methodName(methodName)
                    .argumentValues(argValues)
                    .argumentNames(argNames)
                    .fileRequest(false)
                    .build();
        }//else, is request for file
        else {
            mCallType = CallType.FILE;
            //Get the part of the url before ?, if any
            url = SubString.create(url).endDelimiter("?").getString();
            return Request.builder()
                    .fileRequest(true)
                    .fileName(url)
                    .build();
        }
    }


    @Override
    public IParser getClone() {
        return new UrlParser();
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
