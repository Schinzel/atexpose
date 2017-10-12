package com.atexpose.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;

/**
 * The purpose of this class is to read script files.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
public interface IAtExposeScriptFile<T extends IAtExpose<T>> extends IAtExpose<T> {


    /**
     * @param fileName The name of the script file to read.
     * @return This for chaining.
     */
    default T loadScriptFile(String fileName) {
        Dispatcher scriptFileDispatcher = Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .isSynchronized(true)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
        return this.startDispatcher(scriptFileDispatcher, true);
    }

}
