package com.atexpose.util.sqs;


import com.amazonaws.services.sqs.model.CreateQueueRequest;

public class SqsStandardQueueTest extends AbstractSqsTest {

    public SqsStandardQueueTest() {
        super(
                SqsQueueType.STANDARD,
                new CreateQueueRequest()
                        .withQueueName(getTestQueueName())
                        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
        );
    }


}
