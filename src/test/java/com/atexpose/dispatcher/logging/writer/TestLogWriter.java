package com.atexpose.dispatcher.logging.writer;

import com.atexpose.dispatcher.logging.LoggerType;
import java.util.ArrayList;

/**
 *
 * @author Schinzel
 */
public class TestLogWriter implements ILogWriter {
    public ArrayList<String> mLogEntries = new ArrayList<>();
    

    @Override
    public void log(String logEntry) {
        mLogEntries.add(logEntry);
    }



    @Override
    public void setUp(String dispatcherName, LoggerType loggerType) {
        //For test this method does not have to do anything.
    }
    
}
