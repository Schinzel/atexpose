package io.schinzel.samples.sqs;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.util.sqs.IQueueProducer;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;

/**
 * Sample shows how to add a QueueProducer to @Expose and use the producer to put a
 * message on an AWS SQS queue.
 * <p>
 * Requirements:
 * - AWS credentials that can receive, send and delete messages.
 * - A fifo queue.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue in class AWS.
 * - Run main
 * - Type the following in CLI: sendToQueue MyFirstSqsProducer, 'a fine message indeed!'
 * - There should now be message on the SQS queue.
 * - Terminate with "close" in CLI.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public class Sample_2_Producer {

    public static void main(String[] args) {
        IQueueProducer sqsProducer = SqsProducer.builder()
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueUrl(AWS.QUEUE_URL)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        AtExpose.create()
                //Start a command line interface
                .startDispatcher(CliFactory.cliBuilder().build())
                //Add the SQS producer to @Expose
                .addQueueProducer("MyFirstSqsProducer", sqsProducer);
    }

}
