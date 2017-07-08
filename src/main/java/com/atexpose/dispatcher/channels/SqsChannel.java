package com.atexpose.dispatcher.channels;

import com.atexpose.util.ByteStorage;
import com.atexpose.util.sqs.SqsConsumer;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.state.State;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/**
 * The purpose of this class is to read from SQS.
 * <p>
 * Created by schinzel on 2017-07-05.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
public class SqsChannel implements IChannel {
    private SqsConsumer mSqsConsumer;


    @Override
    public boolean getRequest(ByteStorage request) {
        String message = mSqsConsumer.receive();
        request.add(UTF8.getBytes(message));
        return mSqsConsumer.isAllSystemsWorking();
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
        mSqsConsumer.close();
    }


    @Override
    public IChannel getClone() {
        return new SqsChannel(mSqsConsumer.clone());
    }


    @Override
    public long requestReadTime() {
        return 0;
    }


    @Override
    public String senderInfo() {
        return mSqsConsumer.getQueueUrl();
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .add("QueueUrl", mSqsConsumer.getQueueUrl())
                .build();
    }

}
