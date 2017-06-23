package com.atexpose.dispatcher.logging.format;

import com.atexpose.dispatcher.logging.LogKey;
import org.json.JSONObject;

import java.util.Map;

/**
 * The purpose of this class is to format log entries to a JSON object.
 *
 * @author schinzel
 */
public class JsonFormatter implements ILogFormatter {

    /**
     * @param logData - Contains the data to be rendered
     * @return A log entry to be added to a log.
     */
    @Override
    public String formatLogEntry(Map<LogKey, String> logData) {
        JSONObject jo = new JSONObject((Map) logData);
        return jo.toString();
    }

}
