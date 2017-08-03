package io.schinzel.samples.script_file;

import com.atexpose.AtExpose;

/**
 * In this sample, a script file is loaded. The script file contains a set of requests to execute.
 */

public class Main {

    public static void main(String[] args) {
        AtExpose.create()
                .loadScriptFile("script_file/scriptfile.txt");
    }
}
