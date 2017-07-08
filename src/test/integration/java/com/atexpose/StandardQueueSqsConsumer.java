package com.atexpose;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.atexpose.util.sqs.SqsQueueType;

public class StandardQueueSqsConsumer extends AbstractSqsConsumer {
    public StandardQueueSqsConsumer() {
        super(
                SqsQueueType.STANDARD,
                new CreateQueueRequest()
                        .withQueueName(getTestQueueName())
                        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
        );
    }

}
