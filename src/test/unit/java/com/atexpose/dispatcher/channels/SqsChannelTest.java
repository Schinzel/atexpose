package com.atexpose.dispatcher.channels;

import io.schinzel.awsutils.sqs.SqsConsumer;
import io.schinzel.basicutils.UTF8;
import org.json.JSONObject;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class SqsChannelTest {

    @Test
    public void writeResponse_WriteResponse_ResponseWrittenToSystemOut() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String response = "my response";
        new SqsChannel(mock(SqsConsumer.class)).writeResponse(UTF8.getBytes(response));
        assertThat(outContent.toString("UTF-8")).isEqualTo(response + "\n");
        System.setOut(null);
    }


    @Test
    public void responseWriteTime_ShouldReturn0() {
        long responseWriteTime = new SqsChannel(mock(SqsConsumer.class)).responseWriteTime();
        assertThat(responseWriteTime).isEqualTo(0L);
    }


    @Test
    public void requestReadTime_ShouldReturn0() {
        long requestReadTime = new SqsChannel(mock(SqsConsumer.class)).requestReadTime();
        assertThat(requestReadTime).isEqualTo(0L);
    }


    @Test
    public void senderInfo_ShouldReturnQueue() {
        SqsConsumer mock = mock(SqsConsumer.class);
        when(mock.getQueueUrl()).thenReturn("this is a queue!");
        String senderInfo = new SqsChannel(mock).senderInfo();
        assertThat(senderInfo).isEqualTo("this is a queue!");
    }


    @Test
    public void getState() {
        SqsConsumer mock = mock(SqsConsumer.class);
        when(mock.getQueueUrl()).thenReturn("this is a queue!");
        JSONObject state = new SqsChannel(mock).getState().getJson();
        assertThat(state.getString("Class")).isEqualTo("SqsChannel");
        assertThat(state.getString("QueueUrl")).isEqualTo("this is a queue!");
    }


    @Test
    public void shutdown_ShouldInvokeCloseOnSqsConsumer() {
        SqsConsumer mock = mock(SqsConsumer.class);
        new SqsChannel(mock).shutdown(Thread.currentThread());
        verify(mock, times(1)).close();
    }


    @Test
    public void getClone_ShouldInvokeCloneOnSqsConsumer() {
        SqsConsumer mock = mock(SqsConsumer.class);
        new SqsChannel(mock).getClone();
        verify(mock, times(1)).clone();
    }
}