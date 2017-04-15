package com.atexpose.dispatcher.parser;

import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.IStateNode;
import org.apache.commons.lang3.tuple.Pair;
import io.schinzel.basicutils.EmptyObjects;

/**
 * This is the interface for implementing languages. E.g. XML, JSON and so on.
 *
 * @author Schinzel
 */
public abstract class AbstractParser implements IStateNode {
    private boolean mIsFileRequest = false;
    private String mFilename;
    private String mMethodName = EmptyObjects.EMPTY_STRING;
    private String[] mArgumentNames = EmptyObjects.EMPTY_STRING_ARRAY;
    private String[] mArgumentValues = EmptyObjects.EMPTY_STRING_ARRAY;
    // ----------------------------------------------------------------
    // GENERAL METHODS
    // ----------------------------------------------------------------


    /**
     * @return Returns a clone of the current object.
     */
    public abstract AbstractParser getClone();
    // ----------------------------------------------------------------
    // METHODS FOR PARSING
    // ----------------------------------------------------------------


    /**
     * Clear held data.
     */
    public void clear() {
        mIsFileRequest = false;
        mFilename = EmptyObjects.EMPTY_STRING;
        mMethodName = EmptyObjects.EMPTY_STRING;
        mArgumentNames = EmptyObjects.EMPTY_STRING_ARRAY;
        mArgumentValues = EmptyObjects.EMPTY_STRING_ARRAY;
    }


    /**
     * When a message is received this is the first method invoked.
     * It is intended that the classes that extend this class use methods
     * setMethodRequest and setFileRequest from this method.
     *
     * @param message The message to set.
     */
    public abstract void parseRequest(String message);


    /**
     * @param methodName
     */
    public final void setMethodRequest(String methodName) {
        this.setMethodRequest(methodName, EmptyObjects.EMPTY_STRING_ARRAY, EmptyObjects.EMPTY_STRING_ARRAY);
    }


    public final void setMethodRequest(String methodName, String[] argValues) {
        this.setMethodRequest(methodName, argValues, EmptyObjects.EMPTY_STRING_ARRAY);
    }


    /**
     * Sets the method call in the request
     *
     * @param methodName The name of the method in the request
     * @param argValues  The values of the arguments in the request
     * @param argNames   The names of the arguments in the request.
     */
    public final void setMethodRequest(String methodName, String[] argValues, String[] argNames) {
        mMethodName = methodName;
        mArgumentValues = argValues;
        mArgumentNames = argNames;
        //Reset other variables
        mIsFileRequest = false;
        mFilename = EmptyObjects.EMPTY_STRING;
    }


    /**
     * Sets the file that is requested
     *
     * @param filename The name of the file in the request
     */
    public final void setFileRequest(String filename) {
        mIsFileRequest = true;
        mFilename = filename;
        //Reset other variables
        mMethodName = EmptyObjects.EMPTY_STRING;
        mArgumentNames = EmptyObjects.EMPTY_STRING_ARRAY;
        mArgumentValues = EmptyObjects.EMPTY_STRING_ARRAY;
    }


    /**
     * @return The method name of the message set in the setMessage method.
     */
    public String getMethodName() {
        return mMethodName;
    }


    /**
     * <br>
     * <i>Note</i>, this method returns null for languages that do not support
     * argument names.
     *
     * @return The argument names of the message set in the parseRequest method.
     */
    public String[] getArgumentNames() {
        return mArgumentNames;
    }


    /**
     * @return The argument values of the message set in the parseRequest
     * method.
     */
    public String[] getArgumentValues() {
        return mArgumentValues;
    }


    /**
     * @return True if the request set with parseRequest was a file request
     */
    public final boolean isFileRequest() {
        return mIsFileRequest;
    }


    /**
     * @return The name if the file set with parseRequest
     */
    public String getFileName() {
        return mFilename;
    }


    /**
     * @return
     */
    public Pair<String, RedirectHttpStatus> getRedirect() {
        throw new RuntimeError("Should not be able to get here");
    }


    /**
     * Is there a redirect for this request, default is false and might be
     * overwritten by implementations
     *
     * @return
     */
    public boolean isRedirect() {
        return false;
    }


}
