package io.schinzel;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import io.schinzel.basicutils.RandomUtil;
import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * Få iväg ett meddelande till SQS
 */

public class REMOVE_ME {
    public static void main(String[] args) {
        System.out.println("Started!");
        ConfigVar configVar = ConfigVar.create(".env");
        String awsAccessKey = configVar.getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = configVar.getValue("AWS_SQS_SECRET_KEY");
        System.out.println("Access: '" + awsAccessKey + "'");
        System.out.println("Secret: '" + awsSecretKey + "'");
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        AmazonSQS sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl("https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo")
                .withMessageBody("hello world 2!!")
                .withMessageGroupId("bappidipabpp")
                .withMessageDeduplicationId(RandomUtil.getRandomString(10));
        //.withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
        //sqs.setEndpoint("https://sqs.us-east-2.amazonaws.com");
        System.out.println("All done!");
    }
}
