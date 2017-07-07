package com.atexpose.util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * The purpose of this class is to read messages from an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-02.
 */
@Accessors(prefix = "m")
public class SqsConsumer {
    @Getter(AccessLevel.PRIVATE) private final AmazonSQS mSqsClient;
    @Getter private final String mQueueUrl;
    /** Is set to true if all systems are working */
    @Getter boolean mAllSystemsWorking = true;


    SqsConsumer(AmazonSQS sqsClient, String queueUrl) {
        mSqsClient = sqsClient;
        mQueueUrl = queueUrl;
    }


    @Builder
    SqsConsumer(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        mSqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        mQueueUrl = queueUrl;
    }


    public String receive() {
        List<Message> messages = Collections.emptyList();
        do {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                    .withQueueUrl(mQueueUrl)
                    .withMaxNumberOfMessages(1)
                    .withWaitTimeSeconds(20);
            try {
                messages = mSqsClient.receiveMessage(receiveMessageRequest).getMessages();
            } catch (IllegalStateException e) {
                mAllSystemsWorking = false;
                return "";
            } catch (AmazonSQSException awsException) {
                //If the queue does not exist anymore
                if (awsException.getErrorCode().equals("AWS.SimpleQueueService.NonExistentQueue")) {
                    mAllSystemsWorking = false;
                    return "";
                }
            }
        } while (messages.isEmpty());
        Message message = messages.get(0);
        mSqsClient.deleteMessage(this.getQueueUrl(), message.getReceiptHandle());
        return message.getBody();
    }


    public void close() {
        mSqsClient.shutdown();
    }


    public SqsConsumer clone() {
        return new SqsConsumer(mSqsClient, mQueueUrl);
    }
}
