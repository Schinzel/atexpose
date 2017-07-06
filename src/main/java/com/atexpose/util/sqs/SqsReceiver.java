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
public class SqsReceiver {
    @Getter(AccessLevel.PRIVATE) private final AmazonSQS mSqsClient;
    @Getter private final String mQueueUrl;
    /** Is set to true if close is invoked */
    boolean mInterrupted = false;
    /** Is set to true if such a serious exception occurs that the receiver cannot keep running. */
    boolean mFatalError = false;


    SqsReceiver(AmazonSQS sqsClient, String queueUrl) {
        mSqsClient = sqsClient;
        mQueueUrl = queueUrl;
    }


    @Builder
    SqsReceiver(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl) {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        mSqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        //If there are no queue with argument url
        if (!mSqsClient.listQueues().getQueueUrls().contains(queueUrl)) {
            throw new RuntimeException("No queue with url '" + queueUrl + "' exists in region " + region.getName());
        }
        mQueueUrl = queueUrl;
    }


    public boolean allSystemsWorking() {
        return !(mInterrupted || mFatalError);
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
                mInterrupted = true;
                return "";
            } catch (AmazonSQSException awsException) {
                //If the queue does not exist anymore
                if (awsException.getErrorCode().equals("AWS.SimpleQueueService.NonExistentQueue")) {
                    mFatalError = true;
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


    public SqsReceiver clone() {
        return new SqsReceiver(mSqsClient, mQueueUrl);
    }

}
