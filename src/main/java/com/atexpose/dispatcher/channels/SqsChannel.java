package com.atexpose.dispatcher.channels;

import com.atexpose.util.ByteStorage;
import com.atexpose.util.sqs.SqsReceiver;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The purpose of this class is to read from SQS.
 * <p>
 * Created by schinzel on 2017-07-05.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
public class SqsChannel implements IChannel {
    @Getter(AccessLevel.PACKAGE)
    SqsReceiver mSqsReceiver;


    @Override
    public boolean getRequest(ByteStorage request) {
        String message = this.getSqsReceiver().receive();
        request.add(UTF8.getBytes(message));
        return mSqsReceiver.allSystemsWorking();
    }


    @Override
    public void writeResponse(byte[] response) {
        System.out.println(UTF8.getString(response));
    }


    @Override
    public long responseWriteTime() {
        return 0;
    }


    @Override
    public void shutdown(Thread thread) {
        mSqsReceiver.close();
    }


    @Override
    public IChannel getClone() {
        return new SqsChannel(mSqsReceiver.clone());
    }


    @Override
    public long requestReadTime() {
        return 0;
    }


    @Override
    public String senderInfo() {
        return mSqsReceiver.getQueueUrl();
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .add("QueueUrl", mSqsReceiver.getQueueUrl())
                .build();
    }

}
