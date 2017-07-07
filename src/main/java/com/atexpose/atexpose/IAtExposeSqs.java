package com.atexpose.atexpose;

import com.atexpose.util.sqs.ISqsSender;
import io.schinzel.basicutils.Thrower;
import lombok.NonNull;

import java.util.Map;

/**
 * The purpose of this interface is to send messages to AWS SQS queues.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public interface IAtExposeSqs<T extends IAtExpose<T>> extends IAtExpose<T> {

    Map<String, ISqsSender> getSqsSenderCollection();


    /**
     * Adds the argument AWS SQS sender to the internal collection under the argument name.
     *
     * @param sqsSenderName The name of the SQS sender.
     * @param sqsSender     The SQS sender to store.
     * @return This for chaining.
     */
    default T addSqsSender(@NonNull String sqsSenderName, @NonNull ISqsSender sqsSender) {
        Thrower.throwIfTrue(this.getSqsSenderCollection().containsKey(sqsSenderName))
                .message("Cannot add SQS sender. There already exists a SQS sender with name '" + sqsSenderName + "'");
        getSqsSenderCollection().put(sqsSenderName, sqsSender);
        return this.getThis();
    }


    /**
     * @param sqsSenderName The name of the SQS sender.
     * @param message       The message to send to SQS.
     * @return This for chaining.
     */
    default T sendToSqs(@NonNull String sqsSenderName, @NonNull String message) {
        Thrower.throwIfFalse(this.getSqsSenderCollection().containsKey(sqsSenderName))
                .message("Cannot send message to SQS. No SQS sender with name '" + sqsSenderName + "'");
        this.getSqsSenderCollection().get(sqsSenderName).send(message);
        return this.getThis();
    }

}
