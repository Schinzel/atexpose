package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import lombok.Builder;

public class CliFactory {

    @Builder(builderMethodName = "cliBuilder", builderClassName = "CliBuilder")
    static IDispatcher newCli() {
        return Dispatcher.builder()
                .name("CommandLine")
                .accessLevel(3)
                .isSynchronized(false)
                .channel(new CommandLineChannel())
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
    }

}
