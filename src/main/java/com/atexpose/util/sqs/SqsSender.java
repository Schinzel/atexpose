package com.atexpose.util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import io.schinzel.basicutils.RandomUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * The purpose of this class is to send messages to an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public class SqsSender implements ISqsSender {
    @Getter(AccessLevel.PRIVATE) private final String groupId = "my_group_id";
    @Getter(AccessLevel.PRIVATE) private final String queueUrl;
    @Getter(AccessLevel.PRIVATE) private final AmazonSQS sqsClient;
    @Getter(AccessLevel.PRIVATE) private final SqsQueueType sqsQueueType;


    @Builder
    private SqsSender(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl, SqsQueueType sqsQueueType) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        this.sqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        this.queueUrl = queueUrl;
        this.sqsQueueType = sqsQueueType;
    }


    @Override
    public ISqsSender send(String message) {
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(this.getQueueUrl())
                .withMessageBody(message);
        if (this.getSqsQueueType() == SqsQueueType.FIFO) {
            sendMsgRequest
                    .withMessageGroupId(this.getGroupId())
                    .withMessageDeduplicationId(getDeduplicationId());
        }
        this.getSqsClient().sendMessage(sendMsgRequest);
        return this;
    }


    static String getDeduplicationId() {
        return String.valueOf(System.nanoTime()) + "_" + RandomUtil.getRandomString(10);
    }
}
