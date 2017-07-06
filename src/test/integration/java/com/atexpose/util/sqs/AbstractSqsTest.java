package com.atexpose.util.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.google.common.base.Strings;
import io.schinzel.basicutils.FunnyChars;
import io.schinzel.basicutils.RandomUtil;
import io.schinzel.basicutils.configvar.ConfigVar;
import org.junit.After;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public abstract class AbstractSqsTest {
    private final AmazonSQS mSqs;
    private final String mQueueUrl;
    private final SqsReceiver mSqsReceiver;
    private final SqsSender mSqsSender;


    AbstractSqsTest(SqsQueueType queueType, CreateQueueRequest createRequest) {
        String awsAccessKey = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        this.mSqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        mQueueUrl = mSqs.createQueue(createRequest).getQueueUrl();
        mSqsReceiver = SqsReceiver.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl(mQueueUrl)
                .region(Regions.EU_WEST_1)
                .build();
        mSqsSender = SqsSender.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl(mQueueUrl)
                .sqsQueueType(queueType)
                .region(Regions.EU_WEST_1)
                .build();

    }


    @After
    public void after() {
        mSqs.deleteQueue(mQueueUrl);
    }


    static String getTestQueueName() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm");
        String dateTimeString = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).format(dateTimeFormat);
        return "test_queue_" + RandomUtil.getRandomString(5) + "_" + dateTimeString;
    }


    @Test
    public void send_StandardQueueRandomString_ReceivedStringShouldBeSentString() {
        String expected = RandomUtil.getRandomString(20);
        mSqsSender.send(expected);
        String actual = mSqsReceiver.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_PersianChars_ReceivedStringShouldBeSentString() {
        String expected = FunnyChars.PERSIAN_LETTERS.getString();
        mSqsSender.send(expected);
        String actual = mSqsReceiver.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_OneChar_ReceivedStringShouldBeSentString() {
        String expected = "1";
        mSqsSender.send(expected);
        String actual = mSqsReceiver.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_EmptyString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                mSqsSender.send("")
        );
    }


    @Test
    public void send_NullString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                mSqsSender.send(null)
        );
    }


    @Test
    public void send_MaxSizeMessage_ReceivedStringShouldBeSentString() {
        //Max size message is 256KB which is 262 144 bytes
        int kb256 = 256 * 1024;
        //Create a string that is 256KB
        String expected = Strings.repeat("1234567890", kb256 / 10)
                + Strings.repeat("*", kb256 % 10);
        mSqsSender.send(expected);
        String actual = mSqsReceiver.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void allSystemsWorking_NewInstance_True() {
        assertThat(mSqsReceiver.isAllSystemsWorking()).isTrue();
    }


    @Test
    public void allSystemsWorking_InterruptReceive_True() throws Exception {
        Thread threadReceive = new Thread(() -> {
            //Start waiting for a message
            mSqsReceiver.receive();
        });
        Thread threadClose = new Thread(() -> {
            //Close and thus interrupt receive
            mSqsReceiver.close();
        });
        threadReceive.start();
        threadClose.start();
        threadReceive.join();
        assertThat(mSqsReceiver.isAllSystemsWorking()).isFalse();
    }


    @Test
    public void close_WhileWaitingForMessage_receiveShouldInterruptAndReturn() throws Exception {
        Thread threadReceive = new Thread(() -> {
            //Start waiting for a message
            mSqsReceiver.receive();
        });
        Thread threadClose = new Thread(() -> {
            //Close and thus interrupt receive
            mSqsReceiver.close();
        });
        threadReceive.start();
        long start = System.nanoTime();
        threadClose.start();
        threadReceive.join();
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        //Assert that hang time was less than 10 ms
        assertThat(durationMs).isLessThan(10);
    }


    @Test
    public void cloneReceiver_SendMessages_BothCloneAndOriginalShouldBeAbleToReceive() {
        String message1 = "this_is_a_message_1";
        mSqsSender.send(message1);
        String actual1 = mSqsReceiver.clone().receive();
        assertThat(actual1).isEqualTo(message1);
        String message2 = "this_is_a_message_2";
        mSqsSender.send(message2);
        String actual2 = mSqsReceiver.receive();
        assertThat(actual2).isEqualTo(message2);


    }
}
