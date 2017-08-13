package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.channels.TestChannel;
import com.atexpose.dispatcher.logging.format.JsonFormatter;
import com.atexpose.dispatcher.logging.writer.TestLogWriter;
import com.atexpose.dispatcher.parser.Request;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Schinzel
 */
public class LoggerTest {
    private static int THREAD_NO = 1234;
    private TestLogWriter mLogWriter;
    private Logger mLogger;
    private TestChannel mChannel;
    private LogEntry mLogEntry;


    @Before
    public void before() {
        ICipher mockCipher = Mockito.mock(ICipher.class);
        Mockito.when(mockCipher.encrypt(anyString())).thenReturn("EncryptedString");
        mLogWriter = new TestLogWriter();
        mLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(new JsonFormatter())
                .logWriter(mLogWriter)
                .cipher(mockCipher)
                .build();
        mChannel = new TestChannel();
        mLogEntry = new LogEntry(THREAD_NO, mChannel);

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
        assertEquals(2, logWriter.mLogEntries.size());
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
        assertEquals(1, logWriter.mLogEntries.size());
    }


    @Test
    public void testLogData() {
        String rawIncomingRequest = "MyRequest";
        String response = "MyResponse";
        Request request = Request.builder()
                .argumentNames(Arrays.asList(new String[]{"ArgName1", "ArgName2"}))
                .argumentValues(Arrays.asList(new String[]{"ArgVal1", "ArgVal2"}))
                .methodName("MyMethod")
                .fileName("MyFileName")
                .fileRequest(false)
                .build();
        mLogEntry.setLogData(rawIncomingRequest, response, request);
        //Set Channel
        int writeTime = 788888;
        mChannel.mTestWriteTime = writeTime;
        int readTime = 78464465;
        mChannel.mTestReadTime = readTime;
        String senderInfo = "MySenderInfo";
        mChannel.mSenderInfo = senderInfo;
        //
        mLogger.log(mLogEntry);
        //
        assertEquals(1, mLogWriter.mLogEntries.size());
        JSONObject jo = new JSONObject(mLogWriter.mLogEntries.get(0));
        String methodNameFromLog = jo.getString(LogKey.METHOD_NAME.toString());
        assertEquals("MyMethod", methodNameFromLog);
        //
        String argsFromLog = jo.getString(LogKey.ARGUMENTS.toString());
        String argsShouldbe = "ArgName1='EncryptedString', ArgName2='EncryptedString'";
        assertEquals(argsShouldbe, argsFromLog);
        //
        String fileNameFromLog = jo.getString(LogKey.FILENAME.toString());
        assertEquals("MyFileName", fileNameFromLog);
        //
        assertEquals(THREAD_NO, jo.getInt(LogKey.THREAD.toString()));
        assertEquals(writeTime, jo.getInt(LogKey.WRITE_TIME_IN_MS.toString()));
        assertEquals(readTime, jo.getInt(LogKey.READ_TIME_IN_MS.toString()));
        //
        assertEquals(senderInfo, jo.getString(LogKey.SENDER.toString()));
        //
        assertEquals(response, jo.getString(LogKey.RESPONSE.toString()));
        //
        assertEquals("EncryptedString", jo.getString(LogKey.REQUEST.toString()));
    }

}
