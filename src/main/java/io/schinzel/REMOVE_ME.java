package io.schinzel;

import com.amazonaws.regions.Regions;
import io.schinzel.basicutils.configvar.ConfigVar;
import io.schinzel.basicutils.timekeeper.Timekeeper;

/**
 * Få iväg ett meddelande till SQS
 * Kunna läsa ett meddelande
 * Fatta group id
 */

public class REMOVE_ME {
    public static void main(String[] args) {
        System.out.println("Started!");
        testReceive();
        System.out.println("All done!");
    }


    public static void testReceive() {
        ConfigVar configVar = ConfigVar.create(".env");
        String awsAccessKey = configVar.getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = configVar.getValue("AWS_SQS_SECRET_KEY");
        SqsReceiver sqsReceiver = SqsReceiver.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl("https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo")
                .region(Regions.EU_WEST_1)
                .build();
        String message = sqsReceiver.receive();
        System.out.println("Message '" + message + "'");
    }


    public static void testSend() {
        ConfigVar configVar = ConfigVar.create(".env");
        String awsAccessKey = configVar.getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = configVar.getValue("AWS_SQS_SECRET_KEY");
        SqsSender sqsSender = SqsSender.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .groupId("my_funky_group_id")
                .queueUrl("https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo")
                .region(Regions.EU_WEST_1)
                .build();
        Timekeeper tk = Timekeeper.create().startLap("s1");
        sqsSender.send("s1");
        tk.stopLap().startLap("s2");
        sqsSender.send("s2");
        tk.stopLap().startLap("s3");
        sqsSender.send("s3");
        tk.stopLap().stop().toStr().pln();
    }


}
