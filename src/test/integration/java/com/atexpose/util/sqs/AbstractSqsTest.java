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
    private final SqsConsumer mSqsConsumer;
    private final SqsProducer mSqsProducer;


    AbstractSqsTest(SqsQueueType queueType, CreateQueueRequest createRequest) {
        String awsAccessKey = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
        String awsSecretKey = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        this.mSqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        mQueueUrl = mSqs.createQueue(createRequest).getQueueUrl();
        mSqsConsumer = SqsConsumer.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl(mQueueUrl)
                .region(Regions.EU_WEST_1)
                .build();
        mSqsProducer = SqsProducer.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .queueUrl(mQueueUrl)
                .sqsQueueType(queueType)
                .region(Regions.EU_WEST_1)
                .build();
    }


    @After
    public void after() {
        try {
            mSqs.deleteQueue(mQueueUrl);
        } catch (Exception e) {
            //Ignore
        }
    }


    static String getTestQueueName() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm");
        String dateTimeString = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).format(dateTimeFormat);
        return "test_queue_" + RandomUtil.getRandomString(5) + "_" + dateTimeString;
    }


    @Test
    public void send_StandardQueueRandomString_ReceivedStringShouldBeSentString() {
        String expected = RandomUtil.getRandomString(20);
        mSqsProducer.send(expected);
        String actual = mSqsConsumer.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_PersianChars_ReceivedStringShouldBeSentString() {
        String expected = FunnyChars.PERSIAN_LETTERS.getString();
        mSqsProducer.send(expected);
        String actual = mSqsConsumer.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_OneChar_ReceivedStringShouldBeSentString() {
        String expected = "1";
        mSqsProducer.send(expected);
        String actual = mSqsConsumer.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_EmptyString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                mSqsProducer.send("")
        );
    }


    @Test
    public void send_NullString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                mSqsProducer.send(null)
        );
    }


    @Test
    public void send_MaxSizeMessage_ReceivedStringShouldBeSentString() {
        //Max size message is 256KB which is 262 144 bytes
        int kb256 = 256 * 1024;
        //Create a string that is 256KB
        String expected = Strings.repeat("1234567890", kb256 / 10)
                + Strings.repeat("*", kb256 % 10);
        mSqsProducer.send(expected);
        String actual = mSqsConsumer.receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void allSystemsWorking_NewInstance_True() {
        assertThat(mSqsConsumer.isAllSystemsWorking()).isTrue();
    }


    @Test
    public void allSystemsWorking_InterruptReceive_True() throws Exception {
        Thread threadReceive = new Thread(() -> {
            //Start waiting for a message
            mSqsConsumer.receive();
        });
        Thread threadClose = new Thread(() -> {
            //Close and thus interrupt receive
            mSqsConsumer.close();
        });
        threadReceive.start();
        threadClose.start();
        threadReceive.join();
        assertThat(mSqsConsumer.isAllSystemsWorking()).isFalse();
    }


    @Test
    public void close_WhileWaitingForMessage_receiveShouldInterruptAndReturn() throws Exception {
        Thread threadReceive = new Thread(() -> {
            //Start waiting for a message
            mSqsConsumer.receive();
        });
        Thread threadClose = new Thread(() -> {
            //Close and thus interrupt receive
            mSqsConsumer.close();
        });
        threadReceive.start();
        long start = System.nanoTime();
        threadClose.start();
        threadReceive.join();
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        //Assert that hang time was less than 100 ms (Travis is the reason for 100 instead of 10ms)
        assertThat(durationMs).isLessThan(100);
    }


    @Test
    public void cloneReceiver_SendMessages_BothCloneAndOriginalShouldBeAbleToReceive() {
        String message1 = "this_is_a_message_1";
        mSqsProducer.send(message1);
        String actual1 = mSqsConsumer.clone().receive();
        assertThat(actual1).isEqualTo(message1);
        String message2 = "this_is_a_message_2";
        mSqsProducer.send(message2);
        String actual2 = mSqsConsumer.receive();
        assertThat(actual2).isEqualTo(message2);
    }


    @Test
    public void receive_QueueDeletedWhileReceiving_ThrowsException() throws Exception {
        Thread threadReceive = new Thread(() -> {
            //Start waiting for a message
            mSqsConsumer.receive();
        });
        Thread threadDeleteQueue = new Thread(() -> {
            mSqs.deleteQueue(mQueueUrl);
        });
        threadReceive.start();
        threadDeleteQueue.start();
        threadReceive.join();
        assertThat(mSqsConsumer.isAllSystemsWorking()).isFalse();
    }


    @Test
    public void receive_NonExistingQueue_ThrowsException() throws Exception {
        mSqs.deleteQueue(mQueueUrl);
        String message = mSqsConsumer.receive();
        assertThat(message).isEmpty();
        assertThat(mSqsConsumer.isAllSystemsWorking()).isFalse();
    }

}
