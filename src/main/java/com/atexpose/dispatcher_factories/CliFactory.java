package com.atexpose.dispatcher_factories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.text_parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;

public class CliFactory {

    CliFactory() {
    }

    public static IDispatcher create() {
        return CliFactory.create(null);
    }

    public static IDispatcher create(String startOfTheLine) {
        return Dispatcher.builder()
                .name("CommandLine")
                .accessLevel(3)
                .isSynchronized(false)
                .channel(new CommandLineChannel(startOfTheLine))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
    }

}
