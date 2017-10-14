package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CliFactoryTest {

    @Test
    public void getChannel_NormalSetUp_CommandLineChannel() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getChannel().getClass())
                .isEqualTo(CommandLineChannel.class);
    }


    @Test
    public void getParser_NormalSetUp_CommandLineChannel() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getParser().getClass())
                .isEqualTo(TextParser.class);
    }


    @Test
    public void getWrapper_NormalSetUp_CsvWrapper() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getWrapper().getClass())
                .isEqualTo(CsvWrapper.class);
    }


    @Test
    public void accessLevel_NormalSetUp_3() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_NormalSetUp_CommandLine() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getKey())
                .isEqualTo("CommandLine");
    }


    @Test
    public void threadCount_NormalSetUp_1() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_NormalSetUp_1() {
        Dispatcher cli = (Dispatcher) CliFactory.cliBuilder().build();
        assertThat(cli.isSynchronized())
                .isEqualTo(false);

    }


}