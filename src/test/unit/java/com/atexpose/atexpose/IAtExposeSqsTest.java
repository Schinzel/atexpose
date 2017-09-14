package com.atexpose.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.util.sqs.IQueueProducer;
import io.schinzel.basicutils.collections.namedvalues.ValuesWithKeys;
import lombok.Getter;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class IAtExposeSqsTest {

    private class AtExposeSqs implements IAtExposeSqs<AtExposeSqs> {
        @Getter ValuesWithKeys<QueueProducerWrapper> queueProducers = ValuesWithKeys.create("QueueProducers");
        @Getter ValuesWithKeys<Dispatcher> dispatchers;
        @Getter API API;


        @Override
        public AtExposeSqs getThis() {
            return this;
        }
    }

    private class QueueProducerMock implements IQueueProducer {
        @Getter
        List<String> messages = new ArrayList<>();


        @Override
        public IQueueProducer send(String message) {
            messages.add(message);
            return this;
        }
    }


    @Test
    public void getQueueProducers_AddThreeProducers_SizeShouldBeThree() {
        ValuesWithKeys<QueueProducerWrapper> queueProducers = new AtExposeSqs()
                .addQueueProducer("name1", mock(IQueueProducer.class))
                .addQueueProducer("name2", mock(IQueueProducer.class))
                .addQueueProducer("name3", mock(IQueueProducer.class))
                .getQueueProducers();
        assertThat(queueProducers).hasSize(3);
    }


    @Test
    public void sendToQueue_AddTwoProducers_TheRightProducerShouldGetTheRightMessage() {
        ArgumentCaptor<String> messageCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor2 = ArgumentCaptor.forClass(String.class);
        IQueueProducer mock1 = mock(IQueueProducer.class);
        IQueueProducer mock2 = mock(IQueueProducer.class);
        new AtExposeSqs()
                .addQueueProducer("name1", mock1)
                .addQueueProducer("name2", mock2)
                .sendToQueue("name1", "message1")
                .sendToQueue("name2", "message2")
                .sendToQueue("name1", "message3");
        verify(mock1, times(2)).send(messageCaptor1.capture());
        verify(mock2, times(1)).send(messageCaptor2.capture());
        assertThat(messageCaptor1.getAllValues().get(0)).isEqualTo("message1");
        assertThat(messageCaptor1.getAllValues().get(1)).isEqualTo("message3");
        assertThat(messageCaptor2.getAllValues().get(0)).isEqualTo("message2");

    }


}