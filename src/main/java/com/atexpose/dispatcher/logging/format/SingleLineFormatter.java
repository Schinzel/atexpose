package com.atexpose.dispatcher.logging.format;

import com.atexpose.dispatcher.logging.LogKey;
import java.util.Map;
import io.schinzel.basicutils.EmptyObjects;

/**
 * A single line log format. Example Call time,Sender,Thread,Read time,Execution
 * time,Write time,Method name,Arguments,Filename,Response,Request
 * '2014-11-29
 * 17:56:31','127.0.0.1:49818','4','2','2','-1','throwError','-','','Error:
 * Requested error thrown','GET /call/throwError HTTP/1.1 Host:
 * 127.0.0.1:5555 User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10;
 * rv:33.0) Gecko/20100101 Firefox/33.0 Accept:
 * text/html,application/xhtml+xml,application/xml;q=0.9,;q=0.8 Accept-Language:
 * en-US,en;q=0.5 Accept-Encoding: gzip, deflate Connection: keep-alive
 * ' '2014-11-29
 * 17:56:31','127.0.0.1:49819','9','0','5','-1','','-','favicon.ico','','GET
 * /favicon.ico HTTP/1.1 Host: 127.0.0.1:5555 User-Agent:
 * Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101
 * Firefox/33.0 Accept:
 * text/html,application/xhtml+xml,application/xml;q=0.9,;q=0.8 Accept-Language:
 * en-US,en;q=0.5 Accept-Encoding: gzip, deflate Connection: keep-alive
 * '
 *
 * @author schinzel
 */
public class SingleLineFormatter implements ILogFormatter {

    // ------------------------------------
    // - LOG ENTRY
    // ------------------------------------
    /**
     * Example: MethodName[Delimiter]getTime
     *
     * @return The delimiter between key and value.
     */
    public String getEntryKeyToValueDelimiter() {
        return null;
    }


    /**
     * Example: MethodName:getTime[Delimiter]MethodName:ping
     *
     * @return The delimiter between values.
     */
    public String getEntryValueDelimiter() {
        return ",";
    }


    /**
     *
     * @return The delimiter between log entries. For example line break.
     */
    public String getEntryDelimiter() {
        return "\n";
    }


    /**
     * Example MethodName[Qualifier]getTime[Qualifier]
     *
     * @return The qualifier for values.
     */
    public String getEntryValueQualifier() {
        return EmptyObjects.EMPTY_STRING;
    }


    // ------------------------------------
    // - LOG COMPILATION
    // ------------------------------------
    /**
     *
     *
     * @param value The value to escape
     * @return The argument string escaped.
     */
    @SuppressWarnings("ConstantConditions")
    String escapeValue(String value) {
        if (value == null) {
            return value;
        }
        if (!this.getEntryValueDelimiter().isEmpty()) {
            value = value.replace(this.getEntryValueDelimiter(), "[ValueDelimiter]");
        }
        if (!this.getEntryValueQualifier().isEmpty()) {
            value = value.replace(this.getEntryValueQualifier(), "[EntryQualifier]");
        }
        if (!this.getEntryDelimiter().isEmpty()) {
            value = value.replace(this.getEntryDelimiter(), "[EntryDelimiter]");
        }
        value = value.replace("\r", "[CarriageReturn]");
        return value;
    }


    /**
     *
     * @param logData - Contains the data to be rendered
     * @return A log entry to be added to a log.
     */
    @Override
    public String formatLogEntry(Map<LogKey, String> logData) {
        StringBuilder sb = new StringBuilder();
        String value;
        for (Map.Entry<LogKey, String> entry : logData.entrySet()) {
            //If this is not the first entry
            if (sb.length() > 0) {
                sb.append(this.getEntryValueDelimiter());
            }
            value = entry.getValue();
            value = this.escapeValue(value);
            if (this.getEntryKeyToValueDelimiter() != null) {
                sb.append(entry.getKey());
                sb.append(this.getEntryKeyToValueDelimiter());
            }
            sb.append(this.getEntryValueQualifier());
            sb.append(value);
            sb.append(this.getEntryValueQualifier());
        }
        sb.append(this.getEntryDelimiter());
        return sb.toString();
    }

}
