package com.atexpose.dispatcher.channels;

import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.state.State;

/**
 * @author Schinzel
 */
public class TestChannel implements IChannel {
    public long mTestWriteTime;
    public long mTestReadTime;
    public String mSenderInfo = "DefaultTestSenderInfo";


    @Override
    public long requestReadTime() {
        return mTestReadTime;
    }


    @Override
    public long responseWriteTime() {
        return mTestWriteTime;
    }


    @Override
    public String senderInfo() {
        return mSenderInfo;
    }


    /*
     * METHODS BELOW HERE NOT IMPLEMENTED
     */


    @Override
    public boolean getRequest(ByteStorage request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void writeResponse(byte[] response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void shutdown(Thread thread) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public IChannel getClone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public State getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
