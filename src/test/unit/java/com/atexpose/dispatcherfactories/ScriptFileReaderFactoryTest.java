package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.parser.textparser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ScriptFileReaderFactoryTest extends ScriptFileReaderFactory {

    @Test
    public void getChannel_DefaultSetUp_ScriptFileChannel() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getChannel())
                .isInstanceOf(ScriptFileChannel.class);
    }


    @Test
    public void getParser_DefaultSetUp_TextParser() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getParser())
                .isInstanceOf(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getWrapper())
                .isInstanceOf(CsvWrapper.class);
    }


    @Test
    public void accessLevel_DefaultSetUp_3() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_DefaultSetUp_CommandLine() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getKey())
                .isEqualTo("ScriptFile");
    }


    @Test
    public void threadCount_DefaultSetUp_1() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_DefaultSetUp_True() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.isSynchronized())
                .isEqualTo(true);
    }


    @Test
    public void getLoggers_DefaultSetUp_EmptyList() {
        Dispatcher dispatcher = (Dispatcher) ScriptFileReaderFactory.create("testfiles/MyFile.txt");
        assertThat(dispatcher.getLoggers()).isEqualTo(Collections.EMPTY_LIST);
    }

}