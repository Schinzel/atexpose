package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.channels.TestChannel;
import com.atexpose.dispatcher.logging.format.JsonFormatter;
import com.atexpose.dispatcher.logging.writer.TestLogWriter;
import com.atexpose.dispatcher.parser.TestParser;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Schinzel
 */
public class LoggerTest {
    static int THREAD_NO = 1234;
    TestLogWriter mLogWriter;
    Logger mLogger;
    TestChannel mChannel;
    TestParser mRequestParser;
    LogEntry mLogEntry;


    @Before
    public void before() {
        mLogWriter = new TestLogWriter();
        mLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(new JsonFormatter())
                .logWriter(mLogWriter)
                .crypto(new TestCrypto())
                .build();
        mChannel = new TestChannel();
        mRequestParser = new TestParser();
        mLogEntry = new LogEntry(THREAD_NO, mChannel, mRequestParser);

    }

    @Test
    public void testLog_EventLogger(){
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(new JsonFormatter())
                .logWriter(logWriter)
                .build();
        LogEntry logEntry1 = new LogEntry(THREAD_NO, mChannel, mRequestParser);
        //An even should be logged
        logger.log(logEntry1);
        assertEquals(1, logWriter.mLogEntries.size());
        LogEntry logEntry2 = new LogEntry(THREAD_NO, mChannel, mRequestParser);
        logEntry2.setIsError();
        //An error should be logged
        logger.log(logEntry2);
        assertEquals(2, logWriter.mLogEntries.size());
    }



    @Test
    public void testLog_ErrorLogger(){
        TestLogWriter logWriter = new TestLogWriter();
        Logger logger = Logger.builder()
                .loggerType(LoggerType.ERROR)
                .logFormatter(new JsonFormatter())
                .logWriter(logWriter)
                .build();
        LogEntry logEntry1 = new LogEntry(THREAD_NO, mChannel, mRequestParser);
        //An even should be logged
        logger.log(logEntry1);
        assertEquals(0, logWriter.mLogEntries.size());
        LogEntry logEntry2 = new LogEntry(THREAD_NO, mChannel, mRequestParser);
        logEntry2.setIsError();
        //An error should be logged
        logger.log(logEntry2);
        assertEquals(1, logWriter.mLogEntries.size());
    }


    @Test
    public void testServeralLogEntries() {
        String request = "MyRequest";
        String response = "MyResponse";
        //Set request parser
        mRequestParser.mArgNames = new String[]{"ArgName1", "ArgName2"};
        mRequestParser.mArgValues = new String[]{"ArgVal1", "ArgVal2"};
        String methodName = "MyMethod";
        mRequestParser.mMethodName = methodName;
        String filename = "MyFileName";
        mRequestParser.mFilename = filename;
        String senderInfo = "MySenderInfo";
        mChannel.mSenderInfo = senderInfo;

        //
        for (int i = 0; i < 100; i++) {
            mChannel.mTestWriteTime = i;
            mChannel.mTestReadTime = i * 2;

            mLogEntry.setLogData(request + i, response + i);
            mLogger.log(mLogEntry);
            mLogEntry.cleanUpLogData();
        }
        //
        assertEquals(100, mLogWriter.mLogEntries.size());
        for (int i = 0; i < 100; i++) {
            String logEntryAsString = mLogWriter.mLogEntries.get(i);
            JSONObject jo = new JSONObject(logEntryAsString);
            assertEquals(i, jo.getInt(LogKey.WRITE_TIME_IN_MS.toString()));
            assertEquals(i*2, jo.getInt(LogKey.READ_TIME_IN_MS.toString()));
            assertEquals(TestCrypto.ENC_PREFIX + request + i, jo.getString(LogKey.REQUEST.toString()));
            assertEquals(response + i, jo.getString(LogKey.RESPONSE.toString()));
        }
    }


    @Test
    public void testLogData() {
        String request = "MyRequest";
        String response = "MyResponse";
        mLogEntry.setLogData(request, response);
        //Set request parser
        mRequestParser.mArgNames = new String[]{"ArgName1", "ArgName2"};
        mRequestParser.mArgValues = new String[]{"ArgVal1", "ArgVal2"};
        String methodName = "MyMethod";
        mRequestParser.mMethodName = methodName;
        String filename = "MyFileName";
        mRequestParser.mFilename = filename;
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
        assertEquals(methodName, methodNameFromLog);
        //
        String argsFromLog = jo.getString(LogKey.ARGUMENTS.toString());
        String argsShouldbe = "ArgName1='ENCRYPTED:ArgVal1', ArgName2='ENCRYPTED:ArgVal2'";
        assertEquals(argsShouldbe, argsFromLog);
        //
        String fileNameFromLog = jo.getString(LogKey.FILENAME.toString());
        assertEquals(filename, fileNameFromLog);
        //
        assertEquals(THREAD_NO, jo.getInt(LogKey.THREAD.toString()));
        assertEquals(writeTime, jo.getInt(LogKey.WRITE_TIME_IN_MS.toString()));
        assertEquals(readTime, jo.getInt(LogKey.READ_TIME_IN_MS.toString()));
        //
        assertEquals(senderInfo, jo.getString(LogKey.SENDER.toString()));
        //
        assertEquals(response, jo.getString(LogKey.RESPONSE.toString()));
        //
        assertEquals(TestCrypto.ENC_PREFIX + request, jo.getString(LogKey.REQUEST.toString()));
    }

}
