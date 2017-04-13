package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.state.State;

/**
 * @author Schinzel
 */
public class TestParser extends AbstractParser {
    public String[] mArgNames;
    public String[] mArgValues;
    public String mMethodName;
    public String mFilename;


    @Override
    public String[] getArgumentNames() {
        return mArgNames;
    }


    @Override
    public String[] getArgumentValues() {
        return mArgValues;
    }


    @Override
    public String getMethodName() {
        return mMethodName;
    }


    @Override
    public String getFileName() {
        return mFilename;
    }


    @Override
    public AbstractParser getClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void parseRequest(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public State getState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
