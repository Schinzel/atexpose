package io.schinzel.samples.sample_sqs_consumer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.schinzel.basicutils.RandomUtil;

/**
 * Created by schinzel on 2017-07-06.
 */
class SqsQueue {
    private final AmazonSQS mAmazonSqsClient;
    private final String mQueueUrl;


    SqsQueue(String awsAccessKey, String awsSecretKey) {
        String queueName = "my_atexpose_queue_" + RandomUtil.getRandomString(5) + ".fifo";
        CreateQueueRequest createQueueRequest = new CreateQueueRequest()
                .withQueueName(queueName)
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")
                .addAttributesEntry("FifoQueue", "true")
                .addAttributesEntry("ContentBasedDeduplication", "false");
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        mAmazonSqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        mQueueUrl = mAmazonSqsClient.createQueue(createQueueRequest).getQueueUrl();
    }


    String getQueueUrl() {
        return mQueueUrl;
    }


    void deleteQueue() {
        mAmazonSqsClient.deleteQueue(mQueueUrl);
    }

}

