package com.atexpose.util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.schinzel.basicutils.RandomUtil;
import org.junit.Before;

public class SqsFifoQueueTest extends AbstractSqsTest {

    @Before
    public void before() {
        String queueName = "test_queue_" + RandomUtil.getRandomString(5) + "_" + getDateTime();
        queueName += ".fifo";
        AWSCredentials credentials = new BasicAWSCredentials(mAwsAccessKey, mAwsSecretKey);
        this.mSqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        CreateQueueRequest createRequest = new CreateQueueRequest()
                .withQueueName(queueName)
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
                .addAttributesEntry("FifoQueue", "true")
                .addAttributesEntry("ContentBasedDeduplication", "false");
        mQueueUrl = mSqs.createQueue(createRequest).getQueueUrl();
    }


    @Override
    SqsQueueType getQueueType() {
        return SqsQueueType.FIFO;
    }
}
