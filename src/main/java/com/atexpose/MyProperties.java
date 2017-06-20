package com.atexpose;

/**
 * Module wide properties.
 *
 * @author Schinzel
 */
public class MyProperties {

    /**
     * The encoding used.
     */
    public static final String ENCODING = "UTF-8";
    /**
     * The line separator of the operating system that this code runs on.
     */
    public static final String OS_LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * The system file separator. E.g. on OS X the file separator would be slash
     * as in "/Users/schinzel"
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

}
