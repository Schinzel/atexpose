package com.atexpose;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.SqsConsumerFactory;
import com.atexpose.util.sqs.SqsProducer;
import com.atexpose.util.sqs.SqsQueueType;
import io.schinzel.basicutils.RandomUtil;
import io.schinzel.basicutils.Sandman;
import io.schinzel.basicutils.configvar.ConfigVar;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class AbstractSqsConsumer {
    private final String mAwsAccessKey = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    private final String mAwsSecretKey = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    private final AmazonSQS mSqs;
    private final String mQueueUrl;
    private final SqsQueueType mQueueType;



    AbstractSqsConsumer(SqsQueueType queueType, CreateQueueRequest createRequest) {
        mQueueType = queueType;
        AWSCredentials credentials = new BasicAWSCredentials(mAwsAccessKey, mAwsSecretKey);
        this.mSqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        mQueueUrl = mSqs.createQueue(createRequest).getQueueUrl();
    }


    @After
    public void after() {
        mSqs.deleteQueue(mQueueUrl);
        System.setOut(null);
    }


    @Test
    public void consumerMessageFromSqsQueue() {
        IDispatcher sqsConsumer = SqsConsumerFactory.builder()
                .awsAccessKey(mAwsAccessKey)
                .awsSecretKey(mAwsSecretKey)
                .queueUrl(mQueueUrl)
                .region(Regions.EU_WEST_1)
                .name("MyFirstSqsConsumer")
                .noOfThreads(1)
                .accessLevel(1)
                .build();
        AtExpose.create().start(sqsConsumer);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        SqsProducer.builder()
                .awsAccessKey(mAwsAccessKey)
                .awsSecretKey(mAwsSecretKey)
                .queueUrl(mQueueUrl)
                .sqsQueueType(mQueueType)
                .region(Regions.EU_WEST_1)
                .build()
                .send("{\"method\": \"echo\", \"params\": {\"String\": \"gibbon\"}}");
        //Wait for the message to read from queue.
        Sandman.snoozeSeconds(1);
        assertThat(outContent.toString()).isEqualTo("gibbon\n");

    }


    static String getTestQueueName() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm");
        String dateTimeString = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).format(dateTimeFormat);
        return "test_queue_" + RandomUtil.getRandomString(5) + "_" + dateTimeString;
    }

}