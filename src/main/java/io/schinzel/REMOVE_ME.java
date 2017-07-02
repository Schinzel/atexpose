package io.schinzel;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
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
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        //AmazonSQSClient sqs = new AmazonSQSClient(credentials);
        AmazonSQS amazonSQS = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        //sqs.setEndpoint("https://sqs.us-east-2.amazonaws.com");
        System.out.println("All done!");
    }
}
