package com.atexpose.dispatcher.wrapper.webresponse;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FilenameUtils;

import java.util.Locale;

/**
 * Created by schinzel on 2017-06-03.
 */
class FileUtil {
    static ImmutableSet<String> TEXT_FILE = new ImmutableSet.Builder<String>()
            .add("css")
            .add("html")
            .add("htm")
            .add("js")
            .add("json")
            .add("txt")
            .build();


    private FileUtil() {
    }


    static boolean isTextFile(String filename) {
        String fileExtension = FilenameUtils
                .getExtension(filename)
                .toLowerCase(Locale.US);
        return TEXT_FILE.contains(fileExtension);

    }

}
