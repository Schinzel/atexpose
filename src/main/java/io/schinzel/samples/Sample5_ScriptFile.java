package io.schinzel.samples;

import com.atexpose.AtExpose;

/**
 * <p>
 * The purpose of this sample is to show how a script file is loaded.
 * </p>
 * In this sample, a script file is loaded. The script file contains a set of requests to execute.
 */

public class Sample5_ScriptFile {

    public static void main(String[] args) {
        AtExpose.create()
                .readScriptFile("script_file/script_file.txt");
    }
}
