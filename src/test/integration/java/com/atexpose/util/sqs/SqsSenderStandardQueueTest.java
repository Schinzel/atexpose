package com.atexpose.util.sqs;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.schinzel.basicutils.RandomUtil;
import org.junit.Before;

public class SqsSenderStandardQueueTest extends AbstractSqsSenderTest {


    @Before
    public void before() {
        String queueName = "test_queue_" + RandomUtil.getRandomString(5) + "_" + getDateTime();
        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        this.sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        this.queueUrl = sqs.createQueue(queueName).getQueueUrl();
    }


    @Override
    SqsSender getSender() {
        return SqsSender.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(queueUrl)
                .sqsQueueType(SqsQueueType.STANDARD)
                .region(Regions.EU_WEST_1)
                .build();
    }
}
