package com.atexpose.dispatcher.logging.writer;

import java.util.ArrayList;

/**
 * @author Schinzel
 */
public class TestLogWriter implements ILogWriter {
    public ArrayList<String> mLogEntries = new ArrayList<>();


    @Override
    public void log(String logEntry) {
        mLogEntries.add(logEntry);
    }


}
