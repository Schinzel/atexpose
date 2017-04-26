package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.state.State;

/**
 * @author Schinzel
 */
public class TestParser implements IParser {
    public String[] mArgNames;
    public String[] mArgValues;
    public String mMethodName;
    public String mFilename;


    @Override
    public Request getRequest(String incomingRequest) {
        return Request.builder()
                .methodName(mMethodName)
                .argumentNames(mArgNames)
                .argumentValues(mArgValues)
                .fileName(mFilename)
                .fileRequest((mFilename != null))
                .build();
    }


    @Override
    public IParser getClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public State getState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
