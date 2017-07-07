package io.schinzel.samples.sample_sqs;

import com.amazonaws.regions.Regions;
import com.atexpose.AtExpose;
import com.atexpose.util.sqs.IQueueProducer;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;
import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * '
 * Type the following in CLI:
 * sendToQueue MyFirstSqsProducer, '{"method": "doHeavyBackgroundJob", "params": {"Int": 123}}'
 *
 * Created by schinzel on 2017-07-07.
 */
public class Main {
    static final String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static final String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    static final String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo";


    public static void main(String[] args) {
        setUpConsumer();
        setUpProducer();

    }


    static void setUpConsumer() {
        AtExpose.create()
                //Expose sample class
                .expose(new JobClass())
                //Set up SQS consumer
                .getSqsConsumerBuilder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .name("MyFirstSqsConsumer")
                .noOfThreads(2)
                .accessLevel(1)
                .start();
    }


    static void setUpProducer() {
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
                //Add the queue producer to @Expose
                .addQueueProducer("MyFirstSqsProducer", sqsProducer);
    }
}
