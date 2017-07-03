package io.schinzel;

import com.amazonaws.regions.Regions;
import io.schinzel.basicutils.configvar.ConfigVar;
import io.schinzel.basicutils.timekeeper.Timekeeper;
import com.atexpose.util.sqs.ISqsSender;
import com.atexpose.util.sqs.SqsQueueType;
import com.atexpose.util.sqs.SqsReceiver;
import com.atexpose.util.sqs.SqsSender;

public class REMOVE_ME {
    static String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    static String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_queue.fifo";
    //static String QUEUE_URL = "https://sqs.eu-west-1.amazonaws.com/146535832843/my_first_standard_queue";


    public static void main(String[] args) {
        System.out.println("Started!");
        testReceive();
        System.out.println("All done!");
    }


    public static void testReceive() {
        SqsReceiver sqsReceiver = SqsReceiver.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .region(Regions.EU_WEST_1)
                .build();
        String message = sqsReceiver.receive();
        System.out.println("Message '" + message + "'");
    }


    public static void testSend() {
        ISqsSender sqsSender = SqsSender.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(QUEUE_URL)
                .sqsQueueType(SqsQueueType.FIFO)
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
