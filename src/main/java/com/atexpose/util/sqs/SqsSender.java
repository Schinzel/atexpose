package com.atexpose.util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import io.schinzel.basicutils.RandomUtil;
import io.schinzel.basicutils.Thrower;
import lombok.Builder;

/**
 * The purpose of this class is to send messages to an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public class SqsSender implements ISqsSender {
    private static final String GROUP_ID = "my_group_id";
    private final String mQueueUrl;
    private final AmazonSQS mSqsClient;
    private final SqsQueueType mSqsQueueType;


    @Builder
    SqsSender(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl, SqsQueueType sqsQueueType) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        mSqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        mQueueUrl = queueUrl;
        mSqsQueueType = sqsQueueType;
    }


    @Override
    public ISqsSender send(String message) {
        Thrower.throwIfVarEmpty(message, "message");
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(mQueueUrl)
                .withMessageBody(message);
        if (mSqsQueueType == SqsQueueType.FIFO) {
            sendMsgRequest
                    .withMessageGroupId(GROUP_ID)
                    .withMessageDeduplicationId(getDeduplicationId());
        }
        mSqsClient.sendMessage(sendMsgRequest);
        return this;
    }


    static String getDeduplicationId() {
        return String.valueOf(System.nanoTime()) + "_" + RandomUtil.getRandomString(10);
    }
}
