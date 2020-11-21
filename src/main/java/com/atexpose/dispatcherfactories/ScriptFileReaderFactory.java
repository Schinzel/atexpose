package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.parser.textparser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;

public class ScriptFileReaderFactory {

    ScriptFileReaderFactory() {
    }


    public static IDispatcher create(String fileName) {
        return Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .isSynchronized(true)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
    }
}
