package com.atexpose.dispatcher_factories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.text_parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CliFactoryTest extends CliFactory {

    @Test
    public void getChannel_DefaultSetUp_CommandLineChannel() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(CommandLineChannel.class);
    }


    @Test
    public void getParser_DefaultSetUp_CommandLineChannel() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getParser())
                .isInstanceOf(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getWrapper())
                .isInstanceOf(CsvWrapper.class);
    }


    @Test
    public void accessLevel_DefaultSetUp_3() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_DefaultSetUp_CommandLine() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getKey())
                .isEqualTo("CommandLine");
    }


    @Test
    public void threadCount_DefaultSetUp_1() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_DefaultSetUp_False() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.isSynchronized())
                .isEqualTo(false);
    }


    @Test
    public void getLoggers_DefaultSetUp_EmptyList() {
        Dispatcher dispatcher = (Dispatcher) CliFactory.create();
        assertThat(dispatcher.getLoggers()).isEqualTo(Collections.EMPTY_LIST);
    }

}