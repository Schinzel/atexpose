package io.schinzel.samples.sqs;

/**
 * The purpose of this interface is to send messages to an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public interface ISqsSender {

    ISqsSender send(String message);
}
