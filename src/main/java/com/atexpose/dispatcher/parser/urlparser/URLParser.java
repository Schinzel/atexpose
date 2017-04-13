package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.parser.AbstractParser;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.SubStringer;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
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
    private static final String COMMAND_REQUEST_MARKER = "call/";
    /**
     * Holds redirect hosts
     */
    final List<Redirect> mRedirects;
    final boolean mForceHttps;
    @Getter(AccessLevel.PROTECTED)
    protected HttpRequest mHttpRequest;
    private String mFileNameIncQueryString;
    @Getter(AccessLevel.PROTECTED)
    protected CallType mCallType;


    enum CallType {
        COMMAND, FILE, GHOST, UNKNOWN
    }
    // ---------------------------------
    // - CONSTRUCTORS  -
    // ---------------------------------


    URLParser() {
        this(false);
    }


    URLParser(boolean forceHttps) {
        this(forceHttps, new ArrayList<>());
    }


    public URLParser(boolean forceHttps, List<Redirect> redirects) {
        mForceHttps = forceHttps;
        mRedirects = redirects;
    }


    @Override
    public AbstractParser getClone() {
        return new URLParser(mForceHttps, mRedirects);
    }


    // ---------------------------------
    // - OPERATIONS  -
    // ---------------------------------
    @Override
    public void parseRequest(String request) {
        mCallType = CallType.UNKNOWN;
        if (request.length() == 1) {
            mCallType = CallType.GHOST;
            return;
        }
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
            mFileNameIncQueryString = url;
            //Get the part of the url before ?, if any
            url = SubStringer.create(url).end("?").toString();
            this.setFileRequest(url);
        }
    }


    @Override
    public boolean isGhostCall() {
        return (mCallType == CallType.GHOST);
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
                .add("ForceHttps", mForceHttps)
                .build();
    }


    @Override
    public boolean isRedirect() {
        String hostName = getHostName();
        String fileName = getFileName();
        for (Redirect redirect : mRedirects) {
            if (redirect.isRedirectHost(hostName)) {
                return true;
            }
            if (redirect.isRedirectFile(fileName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @return Pair of path to where we should redirect the user request and type of redirect
     */
    @Override
    public Pair<String, RedirectHttpStatus> getRedirect() {
        String hostName = getHostName();
        for (Redirect redirect : mRedirects) {
            if (redirect.isRedirectHost(hostName)) {
                return redirect.getRedirectInfo(mForceHttps, mFileNameIncQueryString);
            }
            if (redirect.isRedirectFile(mFileNameIncQueryString)) {
                return redirect.getRedirectInfo(mForceHttps, mFileNameIncQueryString);
            }
        }
        return null;
    }


    private String getHostName() {
        return mHttpRequest.getRequestHeaderValue("Host");
    }

}
