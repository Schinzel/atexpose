package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.channels.TestChannel;
import com.atexpose.dispatcher.logging.format.JsonFormatter;
import com.atexpose.dispatcher.logging.writer.TestLogWriter;
import com.atexpose.dispatcher.parser.Request;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Schinzel
 */
public class LoggerTest {
    private static int THREAD_NO = 1234;
    TestLogWriter mLogWriter;
    Logger mLogger;
    TestChannel mChannel;
    LogEntry mLogEntry;


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
    public void log_EventLoggerLogOneEventAndOneError_TwoEntriesInLog() {
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(new JsonFormatter())
                .logWriter(logWriter)
                .build();
        Request request = Request.builder()
                .argumentNames(EmptyObjects.EMPTY_STRING_ARRAY)
                .argumentValues(EmptyObjects.EMPTY_STRING_ARRAY)
                .methodName("")
                .fileName("")
                .fileRequest(false)
                .build();
        LogEntry logEntry1 = new LogEntry(THREAD_NO, mChannel);
        logEntry1.setLogData("RequestAsString", "Reponse", request);
        //An even should be logged
        logger.log(logEntry1);
        assertEquals(1, logWriter.mLogEntries.size());
        LogEntry logEntry2 = new LogEntry(THREAD_NO, mChannel);
        logEntry2.setIsError();
        logEntry2.setLogData("RequestAsString", "Reponse", request);
        //An error should be logged
        logger.log(logEntry2);
        assertEquals(2, logWriter.mLogEntries.size());
    }


    @Test
    public void log_SetUpErrorLoggerAndLogOneEventAndLogOneError_OnlyOneLogEntry() {
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.ERROR)
                .logFormatter(new JsonFormatter())
                .logWriter(logWriter)
                .build();
        Request request = Request.builder()
                .argumentNames(EmptyObjects.EMPTY_STRING_ARRAY)
                .argumentValues(EmptyObjects.EMPTY_STRING_ARRAY)
                .methodName("")
                .fileName("")
                .fileRequest(false)
                .build();
        LogEntry logEntry1 = new LogEntry(THREAD_NO, mChannel);
        //An event should not be logged
        logger.log(logEntry1);
        assertEquals(0, logWriter.mLogEntries.size());
        LogEntry logEntry2 = new LogEntry(THREAD_NO, mChannel);
        logEntry2.setIsError();
        logEntry2.setLogData("RequestAsString", "ResponseAsString", request);
        //An error should be logged
        logger.log(logEntry2);
        assertEquals(1, logWriter.mLogEntries.size());
    }


    @Test
    public void testServeralLogEntries() {
        String rawIncomingRequest = "MyRequest";
        String response = "MyResponse";
        Request request = Request.builder()
                .argumentNames(EmptyObjects.EMPTY_STRING_ARRAY)
                .argumentValues(EmptyObjects.EMPTY_STRING_ARRAY)
                .methodName("")
                .fileName("")
                .fileRequest(false)
                .build();
        for (int i = 0; i < 100; i++) {
            mChannel.mTestWriteTime = i;
            mChannel.mTestReadTime = i * 2;
            mLogEntry.setLogData(rawIncomingRequest + i, response + i, request);
            mLogger.log(mLogEntry);
            mLogEntry.cleanUpLogData();
        }
        assertEquals(100, mLogWriter.mLogEntries.size());
        for (int i = 0; i < 100; i++) {
            String logEntryAsString = mLogWriter.mLogEntries.get(i);
            JSONObject jo = new JSONObject(logEntryAsString);
            assertEquals(i, jo.getInt(LogKey.WRITE_TIME_IN_MS.toString()));
            assertEquals(i * 2, jo.getInt(LogKey.READ_TIME_IN_MS.toString()));
            assertEquals("EncryptedString", jo.getString(LogKey.REQUEST.toString()));
            assertEquals(response + i, jo.getString(LogKey.RESPONSE.toString()));
        }
    }


    @Test
    public void testLogData() {
        String rawIncomingRequest = "MyRequest";
        String response = "MyResponse";
        Request request = Request.builder()
                .argumentNames(new String[]{"ArgName1", "ArgName2"})
                .argumentValues(new String[]{"ArgVal1", "ArgVal2"})
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
