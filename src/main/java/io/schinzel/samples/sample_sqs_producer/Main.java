package io.schinzel.samples.sample_sqs_producer;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.util.sqs.IQueueProducer;
import com.atexpose.util.sqs.SqsQueueType;
import com.atexpose.util.sqs.SqsProducer;
import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * This sample sends message from a AWS SQS.
 * Requirements:
 * - AWS credentials that can receive, send and delete messages.
 * - A fifo queue.
 * <p>
 * Instructions:
 * - Set access key, secret key and the queue url below
 * - Run main
 * - Type the following in CLI: sendToQueue MyFirstSqsProducer, "a fine message indeed!"
 * - Terminate with "close" in CLI.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public class Main {
    static final String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static final String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    static final String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo";


    public static void main(String[] args) {
        IQueueProducer sqsProducer = SqsProducer.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueUrl(QUEUE_URL)
                .sqsQueueType(SqsQueueType.FIFO)
                .build();
        AtExpose.create()
                //Start a command line interface
                .startCLI()
                //Add the SQS producer to @Expose
                .addQueueProducer("MyFirstSqsProducer", sqsProducer);
    }

}
