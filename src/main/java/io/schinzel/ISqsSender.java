package io.schinzel;

/**
 * The purpose of this interface is to send messages to a SQS queue.
 *
 * Created by schinzel on 2017-07-03.
 */
public interface ISqsSender {

    ISqsSender send(String message);
}
