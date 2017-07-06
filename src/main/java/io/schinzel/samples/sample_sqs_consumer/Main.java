package io.schinzel.samples.sample_sqs_consumer;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.util.sqs.SqsQueueType;
import com.atexpose.util.sqs.SqsSender;
import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * Requirements:
 * This sample requires an Amazon Web Services credentials to a user that
 * has rights to create and delete queues and send and receive messages from SQS.
 * The credentials should be environment variables named as below. Or in a file named .env.
 * <p>
 * Created by schinzel on 2017-07-06.
 */
public class Main {
    static final String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo";
    static final String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static final String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");


    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI().expose(new JobClass());
        atExpose.getSqsConsumerBuilder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .name("MyFirstSqsConsumer")
                .noOfThreads(1)
                .accessLevel(1)
                .region(Regions.EU_WEST_1)
                .queueUrl(QUEUE_URL)
                .start();
        SqsSender sqsSender = SqsSender.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        for (int i = 0; i < 5; i++) {
            sqsSender.send("{\"method\": \"doHeavyBackgroundJob\", \"params\": {\"Int\": " + i + "}}");
        }
        //atExpose.shutdown();
    }


}

