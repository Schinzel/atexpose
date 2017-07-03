package io.schinzel.samples.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * The purpose of this class is to send messages to a SQS standard queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
public class SqsStandardQueueSender implements ISqsSender {
    @Getter(AccessLevel.PRIVATE) private final String queueUrl;
    @Getter(AccessLevel.PRIVATE) private final AmazonSQS sqsClient;


    @Builder
    private SqsStandardQueueSender(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        this.sqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        this.queueUrl = queueUrl;
    }


    public SqsStandardQueueSender send(String message) {
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(this.getQueueUrl())
                .withMessageBody(message);
        this.getSqsClient().sendMessage(sendMsgRequest);
        return this;
    }


}
