package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import lombok.Builder;

public class ScriptFileReaderFactory {

    @Builder(builderMethodName = "scriptFileReader", builderClassName = "ScriptFileReaderBuilder")
    static IDispatcher newScriptFileReader(String fileName) {
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
