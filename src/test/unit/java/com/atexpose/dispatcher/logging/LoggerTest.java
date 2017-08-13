package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.format.JsonFormatter;
import com.atexpose.dispatcher.logging.writer.SystemOutLogWriter;
import com.atexpose.dispatcher.logging.writer.TestLogWriter;
import io.schinzel.basicutils.crypto.cipher.NoCipher;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;


public class LoggerTest {

    @Test
    public void constructor_NoArgs_EventLogger() {
        Logger logger = Logger.builder().build();
        assertThat(logger.mLoggerType).isEqualByComparingTo(LoggerType.EVENT);
    }


    @Test
    public void constructor_NoArgs_LogWriterIsSystemOut() {
        Logger logger = Logger.builder().build();
        assertThat(logger.mLogWriter).isInstanceOf(SystemOutLogWriter.class);
    }


    @Test
    public void constructor_NoArgs_FormatterIsJson() {
        Logger logger = Logger.builder().build();
        assertThat(logger.mLogFormatter).isInstanceOf(JsonFormatter.class);
    }


    @Test
    public void constructor_NoArgs_CipherIsNoCipher() {
        Logger logger = Logger.builder().build();
        assertThat(logger.mCipher).isInstanceOf(NoCipher.class);
    }


    @Test
    public void log_Log1EventAnd1ErrorInAnEventLogger_BothEntriesInLog() {
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logWriter(logWriter)
                .build();
        ILogEntry eventLogEntry = Mockito.mock(ILogEntry.class);
        Mockito.when(eventLogEntry.isError()).thenReturn(true);
        logger.log(eventLogEntry);
        ILogEntry errorLogEntry = Mockito.mock(ILogEntry.class);
        Mockito.when(errorLogEntry.isError()).thenReturn(false);
        logger.log(errorLogEntry);
        assertThat(logWriter.mLogEntries).hasSize(2);
    }


    @Test
    public void log_Log1EventAnd1ErrorInAnErrorLogger_OnlyOneLogEntry() {
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.ERROR)
                .logWriter(logWriter)
                .build();
        ILogEntry eventLogEntry = Mockito.mock(ILogEntry.class);
        Mockito.when(eventLogEntry.isError()).thenReturn(true);
        logger.log(eventLogEntry);
        ILogEntry errorLogEntry = Mockito.mock(ILogEntry.class);
        Mockito.when(errorLogEntry.isError()).thenReturn(false);
        logger.log(errorLogEntry);
        assertThat(logWriter.mLogEntries).hasSize(1);
    }


}
