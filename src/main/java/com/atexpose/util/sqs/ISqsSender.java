package com.atexpose.util.sqs;

import io.schinzel.basicutils.collections.keyvalues.IValueKey;

/**
 * The purpose of this interface is to send messages to an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public interface ISqsSender extends IValueKey {

    /**
     * Sends the argument message to an AWS SQS queue.
     *
     * @param message The message to send.
     * @return This for chaining.
     */
    ISqsSender send(String message);
}
