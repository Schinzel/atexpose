package com.atexpose.dispatcher.wrapper;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FilenameUtils;

import java.util.Locale;

/**
 * Various file util methods
 *
 * Created by schinzel on 2017-06-03.
 */
class FileUtil {
    private static final ImmutableSet<String> TEXT_FILE = new ImmutableSet.Builder<String>()
            .add("css")
            .add("html")
            .add("htm")
            .add("js")
            .add("json")
            .add("txt")
            .build();


    /**
     * Private constructor as should not be created
     */
    private FileUtil() {
    }


    /**
     *
     * @param filename The name of the file to check
     * @return True if the argument file name has a text file extension
     */
    static boolean isTextFile(String filename) {
        String fileExtension = FilenameUtils
                .getExtension(filename)
                .toLowerCase(Locale.US);
        return TEXT_FILE.contains(fileExtension);

    }


    /**
     * Method to test if request is for a file or folder
     *
     * @param fileOrPath The string to check
     * @return True if the argument is a path to a dir
     */
    static boolean isDirPath(String fileOrPath) {
        int lastSlash = fileOrPath.lastIndexOf('/');
        int lastDot = fileOrPath.lastIndexOf('.');
        // if we have not dot, it is a folder
        // if we have a slash after the last dot, it is a folder
        // else it is a file
        return lastDot == -1 || lastSlash > lastDot;
    }
}
