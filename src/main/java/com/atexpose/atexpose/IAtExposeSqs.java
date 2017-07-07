package com.atexpose.atexpose;

import com.atexpose.util.sqs.ISqsSender;
import io.schinzel.basicutils.collections.keyvalues.KeyValues;

/**
 * The purpose of this interface is to send messages to AWS SQS queues.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public interface IAtExposeSqs<T extends IAtExpose<T>> extends IAtExpose<T> {

    /**
     * @return The collection of AWS SQS senders.
     */
    KeyValues<ISqsSender> getSqsSenders();


    /**
     * Adds the argument AWS SQS sender to the internal collection under the argument name.
     *
     * @param sqsSender The SQS sender to store.
     * @return This for chaining.
     */
    default T addSqsSender(ISqsSender sqsSender) {
        this.getSqsSenders().add(sqsSender);
        return this.getThis();
    }


    /**
     * @param sqsSenderName The name of the SQS sender.
     * @param message       The message to send to SQS.
     * @return This for chaining.
     */
    default T sendToSqs(String sqsSenderName, String message) {
        this.getSqsSenders().get(sqsSenderName).send(message);
        return this.getThis();
    }

}
