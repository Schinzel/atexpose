package io.schinzel.samples.sqs;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.dispatcherfactories.SqsConsumerFactory;
import com.atexpose.util.sqs.IQueueProducer;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;

/**
 * The sample sets up a consumer and a producer. A message is put in an AWS SQS queue using the
 * CLI. The message is the consumed by the consumer and the request executed.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue url in class AWS.
 * - Run main
 * - Type the following in CLI: sendToQueue MyFirstSqsProducer, '{"method": "doHeavyBackgroundJob",
 * "params": {"Int": 123}}'
 * - Type close in CLI to quit.
 * <p>
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public class Sample_3_ConsumerAndProducer {


    public static void main(String[] args) {
        setUpConsumer();
        setUpProducer();

    }


    static void setUpConsumer() {
        AtExpose.create()
                //Expose sample class
                .expose(new JobClass())
                //Start SQS consumer
                .startDispatcher(getSqsConsumer());
    }


    private static IDispatcher getSqsConsumer() {
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


    static void setUpProducer() {
        //Create a SqsProducer that can put messages on an AWS SQS queue.
        IQueueProducer sqsProducer = SqsProducer.builder()
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueUrl(AWS.QUEUE_URL)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        AtExpose.create()
                //Start a command line interface
                .startDispatcher(CliFactory.create())
                //Add the queue producer to @Expose
                .addQueueProducer("MyFirstSqsProducer", sqsProducer);
    }
}
