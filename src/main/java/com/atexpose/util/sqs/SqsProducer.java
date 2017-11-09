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
import lombok.experimental.Accessors;

/**
 * The purpose of this class is to send messages to an AWS SQS queue.
 * <p>
 * Created by schinzel on 2017-07-03.
 */
@Accessors(prefix = "m")
public class SqsProducer implements IQueueProducer {
    private static final String GROUP_ID = "my_group_id";
    private final String mQueueUrl;
    private final AmazonSQS mSqsClient;
    private final SqsQueueType mSqsQueueType;


    @Builder
    SqsProducer(String awsAccessKey, String awsSecretKey, Regions region, String queueUrl, SqsQueueType sqsQueueType) {
        Thrower.throwIfVarEmpty(queueUrl, "queueUrl");
        Thrower.throwIfVarNull(sqsQueueType, "sqsQueueType");
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        mSqsClient = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        mQueueUrl = queueUrl;
        mSqsQueueType = sqsQueueType;
    }


    public IQueueProducer send(String message) {
        Thrower.throwIfVarEmpty(message, "message");
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(mQueueUrl)
                .withMessageBody(message);
        if (mSqsQueueType == SqsQueueType.FIFO) {
            sendMsgRequest
                    //Set a group id. As this is not used currently, it is set to a hard coded value
                    .withMessageGroupId(GROUP_ID)
                    //Add a unique if to the message which is used to prevent that the message is duplicated
                    .withMessageDeduplicationId(getUniqueId());
        }
        mSqsClient.sendMessage(sendMsgRequest);
        return this;
    }


    /**
     * @return A unique id.
     */
    private static String getUniqueId() {
        return String.valueOf(System.nanoTime()) + "_" + RandomUtil.getRandomString(10);
    }


}
