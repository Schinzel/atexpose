package com.atexpose;

/**
 * The purpose of this class is to hold properties used throughout the project.
 *
 * @author Schinzel
 */
public class ProjectProperties {
    private ProjectProperties(){}
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
