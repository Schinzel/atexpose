package com.atexpose.dispatcher_factories;

import com.amazonaws.regions.Regions;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.SqsChannel;
import com.atexpose.dispatcher.parser.JsonRpcParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import io.schinzel.samples.auxiliary.AWS;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SqsConsumerFactoryTest extends SqsConsumerFactory {

    @Test
    public void getChannel_DefaultSetUp_SqsChannel() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(1)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(SqsChannel.class);
    }


    @Test
    public void getParser_DefaultSetUp_JsonRpcParser() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(1)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getParser())
                .isInstanceOf(JsonRpcParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(1)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getWrapper())
                .isInstanceOf(CsvWrapper.class);
    }


    @Test
    public void accessLevel_2_2() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getAccessLevel())
                .isEqualTo(2);
    }


    @Test
    public void getKey_MyConsumerName_MyConsumerName() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getKey())
                .isEqualTo("MyConsumerName");
    }


    @Test
    public void getKey_EmptyString_SqsConsumer() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getKey())
                .isEqualTo("SqsConsumer");
    }


    @Test
    public void threadCount_17_17() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getThreadNumber())
                .isEqualTo(17);
    }


    @Test
    public void isSynchronized_DefaultSetUp_False() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.isSynchronized())
                .isFalse();
    }


    @Test
    public void getLoggers_DefaultSetUp_EmptyList() {
        Dispatcher dispatcher = (Dispatcher) SqsConsumerFactory.builder()
                .name("MyConsumerName")
                .awsAccessKey(AWS.ACCESS_KEY)
                .awsSecretKey(AWS.SECRET_KEY)
                .region(Regions.EU_WEST_1)
                .queueName("MyQueueName.fifo")
                .accessLevel(2)
                .noOfThreads(17)
                .build();
        assertThat(dispatcher.getLoggers())
                .isEqualTo(Collections.EMPTY_LIST);
    }

}