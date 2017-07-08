package com.atexpose.atexpose;

import com.atexpose.util.sqs.IQueueProducer;
import io.schinzel.basicutils.collections.keyvalues.IValueKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.experimental.Accessors;

/**
 * The purpose of this class is to wrap a QueueProducer and add:
 * 1) IStateNode so that the state can easily compiled
 * 2) IValueKey so that these can be added to a KeyValue collection.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
@Builder
@Accessors(prefix = "m")
public class QueueProducerWrapper implements IStateNode, IValueKey {
    String mQueueProducerName;
    IQueueProducer mQueueProducer;


    /**
     * Sends the argument message to a queue.
     *
     * @param message The message to send.
     * @return This for chaining.
     */
    public QueueProducerWrapper send(String message) {
        mQueueProducer.send(message);
        return this;
    }


    @Override
    public String getKey() {
        return mQueueProducerName;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", mQueueProducerName)
                .add("Class", mQueueProducer.getClass().getSimpleName())
                .build();
    }

}
