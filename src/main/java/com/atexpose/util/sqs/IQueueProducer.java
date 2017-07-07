package com.atexpose.util.sqs;

/**
 * The purpose of this interface is to send messages to a queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public interface IQueueProducer {

    /**
     * Sends the argument message to a queue.
     *
     * @param message The message to send.
     * @return This for chaining.
     */
    IQueueProducer send(String message);
}
