package com.atexpose.atexpose;

import com.atexpose.util.sqs.IQueueProducer;
import io.schinzel.basicutils.collections.keyvalues.KeyValues;

/**
 * The purpose of this interface is to send messages to AWS SQS queues.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public interface IAtExposeSqs<T extends IAtExpose<T>> extends IAtExpose<T> {

    /**
     * @return The collection of queue producers.
     */
    KeyValues<QueueProducerWrapper> getQueueProducers();


    /**
     * Adds the argument queue producer to the internal collection of queue producers.
     *
     * @param queueProducer The queue producer to store.
     * @return This for chaining.
     */
    default T addQueueProducer(String queueProducerName, IQueueProducer queueProducer) {
        QueueProducerWrapper atexposeQueueProducer = QueueProducerWrapper.builder()
                .queueProducer(queueProducer)
                .queueProducerName(queueProducerName)
                .build();
        this.getQueueProducers().add(atexposeQueueProducer);
        return this.getThis();
    }


    /**
     * @param queueProducerName The name of the queue producer. The producer has to have been added
     *                          to @Expose using method addQueueProducer.
     * @param message           The message to send.
     * @return This for chaining.
     */
    default T sendToQueue(String queueProducerName, String message) {
        this.getQueueProducers().get(queueProducerName).send(message);
        return this.getThis();
    }

}
