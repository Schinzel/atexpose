package com.atexpose.util.sqs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
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


public abstract class AbstractSqsSenderTest {
    static final String AWS_ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    static final String AWS_SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    AmazonSQS sqs;
    String queueUrl;


    static String getDateTime() {
        DateTimeFormatter dataTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm");
        return LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).format(dataTimeFormat);
    }


    abstract SqsSender getSender();


    SqsReceiver getReceiver() {
        return SqsReceiver.builder()
                .awsAccessKey(AWS_ACCESS_KEY)
                .awsSecretKey(AWS_SECRET_KEY)
                .queueUrl(queueUrl)
                .region(Regions.EU_WEST_1)
                .build();
    }


    @After
    public void after() {
        sqs.deleteQueue(queueUrl);
    }


    @Test
    public void send_StandardQueueRandomString_ReceivedStringShouldBeSentString() {
        String expected = RandomUtil.getRandomString(20);
        this.getSender().send(expected);
        String actual = this.getReceiver().receive();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void send_FunnyChars_ReceivedStringShouldBeSentString() {
        for (FunnyChars funnyChars : FunnyChars.values()) {
            String expected = funnyChars.getString();
            this.getSender().send(expected);
            String actual = this.getReceiver().receive();
            assertThat(actual).isEqualTo(expected);
        }
    }


    @Test
    public void send_EmptyString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getSender().send("")
        );
    }


    @Test
    public void send_NullString_ThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                this.getSender().send(null)
        );
    }


    @Test
    public void send_MaxSizeMessage_ReceivedStringShouldBeSentString() {
        //Max size message is 256KB which is 262 144 bytes
        int kb256 = 256 * 1024;
        //Create a string that is 256KB
        String expected = Strings.repeat("1234567890", kb256 / 10)
                + Strings.repeat("*", kb256 % 10);
        this.getSender().send(expected);
        String actual = this.getReceiver().receive();
        assertThat(actual).isEqualTo(expected);

    }

}
