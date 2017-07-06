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


    public static void main(String[] args) {
        String awsAccessKey = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
        SqsQueue sqsQueue = new SqsQueue(awsAccessKey, awsSecretKey);
        String queueUrl = sqsQueue.getQueueUrl();
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI().expose(new JobClass());
        atExpose.getSqsConsumerBuilder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .name("MyFirstSqsConsumer")
                .noOfThreads(1)
                .accessLevel(1)
                .region(Regions.EU_WEST_1)
                .queueUrl(queueUrl)
                .start();
        SqsSender sqsSender = SqsSender.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl(queueUrl)
                .region(Regions.EU_WEST_1)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        sqsSender.send("{\"method\": \"doHeavyBackgroundJob\"}");
        int apa = 44;
        atExpose.shutdown();
        sqsQueue.deleteQueue();

    }


}

