package io.schinzel.samples.sample_sqs_consumer;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.util.sqs.SqsQueueType;
import com.atexpose.util.sqs.SqsSender;
import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * This sample reads messages from a AWS SQS.
 * Requirements:
 * - AWS credentials that can receive, send and delete messages.
 * - A fifo queue.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue url below
 * <p>
 * Created by schinzel on 2017-07-06.
 */
public class Main {
    static final String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static final String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    static final String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo";


    public static void main(String[] args) {
        typicalSetUp();
        sampleCode();
    }


    /**
     * Typical code to set up a SQS.
     */
    static void typicalSetUp() {
        AtExpose atExpose = AtExpose.create();
        //Expose a sample class
        atExpose.getAPI().expose(new JobClass());
        //Start a command line interface
        atExpose.startCLI();
        //Set up SQS consumer
        atExpose.getSqsConsumerBuilder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .name("MyFirstSqsConsumer")
                .noOfThreads(2)
                .accessLevel(1)
                .start();
    }


    /**
     * Sample only code
     */
    static void sampleCode() {
        //Create object that can send messages to SQS.
        SqsSender sqsSender = SqsSender.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        //Send 5 message to the SQS.
        for (int i = 0; i < 5; i++) {
            sqsSender.send("{\"method\": \"doHeavyBackgroundJob\", \"params\": {\"Int\": " + (i + 1) + "}}");
        }
    }

}

