package com.atexpose.util.sqs;

import com.amazonaws.services.sqs.model.CreateQueueRequest;

public class SqsFifoQueueTest extends AbstractSqsTest {

    public SqsFifoQueueTest() {
        super(
                SqsQueueType.FIFO,
                new CreateQueueRequest()
                        .withQueueName(getTestQueueName() + ".fifo")
                        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
                        .addAttributesEntry("FifoQueue", "true")
                        .addAttributesEntry("ContentBasedDeduplication", "false")
        );
    }


}
