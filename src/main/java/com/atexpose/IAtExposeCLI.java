package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;

/**
 * The purpose of this class is to start a command line interface.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
interface IAtExposeCLI<T extends IAtExpose<T>> extends IAtExpose<T> {


    /**
     * Start command line interface.
     */
    default T startCLI() {
        Dispatcher dispatcher = Dispatcher.builder()
                .name("CommandLine")
                .accessLevel(3)
                .channel(new CommandLineChannel())
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(this.getAPI())
                .build();
        return this.startDispatcher(dispatcher, false, false);
    }

}
