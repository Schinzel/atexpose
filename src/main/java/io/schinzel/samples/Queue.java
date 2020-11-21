package io.schinzel.samples;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.CliFactory;
import com.atexpose.dispatcher_factories.SqsConsumerFactory;
import com.atexpose.util.sqs.JsonRpc;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;
import io.schinzel.samples.auxiliary.MyObject;
import io.schinzel.samples.auxiliary.AWS;

/**
 * This sample sets up a consumer and consumes messages from a AWS SQS queue. The sample also
 * shows how to use a utility class to put messages on an AWS SQS queue.
 * <p>
 * Sample produces (writes) a set of messages that are subsequently consumed
 * and the requests in the messages are executed.
 * <p>
 * Requirements:
 * - AWS credentials that can receive, send and delete messages on an SQS queue.
 * - A fifo queue.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue url in class AWS.
 * - Run main
 * - Type "close" in CLI to quit.
 * <p>
 * Created by schinzel on 2017-07-06.
 */
public class Queue {


    public static void main(String[] args) {
        startAtExposeWithConsumer();
        sendRequestsWithProducer();
    }


    /**
     * Typical code to set up a SQS.
     */
    static void startAtExposeWithConsumer() {
        AtExpose.create()
                //Expose a sample class
                .expose(new MyObject())
                //Start a command line interface
                .start(CliFactory.create())
                //Start up SQS consumer
                .start(getConsumer());
    }


    private static IDispatcher getConsumer() {
        return SqsConsumerFactory.builder()
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
    static void sendRequestsWithProducer() {
        //Create object that can add messages to the SQS queue
        SqsProducer sqsProducer = SqsProducer.builder()
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .queueUrl(AWS.QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        //Send 5 messages to SQS queue
        for (int i = 0; i < 5; i++) {
            String jsonRpc = JsonRpc.builder()
                    .methodName("doHeavyBackgroundJob")
                    .argument("Int", String.valueOf(i))
                    .build()
                    .toString();
            sqsProducer.send(jsonRpc);
        }
    }

}

