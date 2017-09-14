package com.atexpose.dispatcher.logging.format;

/**
 * A multi line log format
 *
 * @author schinzel
 */
public class MultiLineFormatter extends SingleLineFormatter {

    @Override
    public String getEntryKeyToValueDelimiter() {
        return ": ";
    }


    @Override
    public String getEntryValueDelimiter() {
        return "\n";
    }


    @Override
    public String getEntryDelimiter() {
        return "\n************************************************************\n\n";
    }


    @Override
    public String getEntryValueQualifier() {
        return "\"";
    }


    @Override
    public String escapeValue(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\r", "");
    }

}
