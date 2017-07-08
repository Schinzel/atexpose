package com.atexpose;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.atexpose.util.sqs.SqsQueueType;

public class FifoQueueSqsConsumer extends AbstractSqsConsumer {
    public FifoQueueSqsConsumer() {
        super(SqsQueueType.FIFO,
                new CreateQueueRequest()
                        .withQueueName(getTestQueueName() + ".fifo")
                        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
                        .addAttributesEntry("FifoQueue", "true")
                        .addAttributesEntry("ContentBasedDeduplication", "false")
        );
    }

}
