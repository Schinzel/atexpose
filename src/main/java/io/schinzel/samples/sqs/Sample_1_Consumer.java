package io.schinzel.samples.sqs;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.dispatcherfactories.SqsConsumerFactory;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;

/**
 * This sample sets up a consumer and consumes messages from a AWS SQS queue. The sample also
 * shows how to use the a utility class to put messages on an AWS SQS queue.
 * <p>
 * Sample produces (writes) a set of messages that are subsequently consumed
 * and the requests in the messages are executed.
 * <p>
 * Requirements:
 * - AWS credentials that can receive, send and delete messages.
 * - A fifo queue.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue url in class AWS.
 * - Run main
 * - Type "close" in CLI to quit.
 * <p>
 * Created by schinzel on 2017-07-06.
 */
public class Sample_1_Consumer {


    public static void main(String[] args) {
        typicalSetUp();
        sampleCode();
    }


    /**
     * Typical code to set up a SQS.
     */
    static void typicalSetUp() {
        AtExpose.create()
                //Expose a sample class
                .expose(new JobClass())
                //Start a command line interface
                .startDispatcher(CliFactory.cliBuilder().build())
                //Start up SQS consumer
                .startDispatcher(getConsumer());
    }


    private static IDispatcher getConsumer() {
        return SqsConsumerFactory.sqsConsumerBuilder()
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .queueUrl(AWS.QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .name("MyFirstSqsConsumer")
                .noOfThreads(2)
                .accessLevel(1)
                .build();
    }


    /**
     * Sample only code
     */
    static void sampleCode() {
        //Create object that can send messages to SQS.
        SqsProducer sqsProducer = SqsProducer.builder()
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .queueUrl(AWS.QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        //Send 5 message to the SQS.
        for (int i = 0; i < 5; i++) {
            sqsProducer.send("{\"method\": \"doHeavyBackgroundJob\", \"params\": {\"Int\": " + (i + 1) + "}}");
        }
    }

}

