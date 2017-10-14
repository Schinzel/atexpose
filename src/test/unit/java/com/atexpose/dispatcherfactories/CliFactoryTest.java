package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CliFactoryTest {

    @Test
    public void getChannel_DefaultSetUp_CommandLineChannel() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getChannel().getClass())
                .isEqualTo(CommandLineChannel.class);
    }


    @Test
    public void getParser_DefaultSetUp_CommandLineChannel() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getParser().getClass())
                .isEqualTo(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getWrapper().getClass())
                .isEqualTo(CsvWrapper.class);
    }


    @Test
    public void accessLevel_DefaultSetUp_3() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_DefaultSetUp_CommandLine() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getKey())
                .isEqualTo("CommandLine");
    }


    @Test
    public void threadCount_DefaultSetUp_1() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_DefaultSetUp_False() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.isSynchronized())
                .isEqualTo(false);
    }


    @Test
    public void getLoggers_DefaultSetUp_EmptyList() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getLoggers()).isEqualTo(Collections.EMPTY_LIST);
    }

}