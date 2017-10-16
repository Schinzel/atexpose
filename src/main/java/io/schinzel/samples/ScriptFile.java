package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcherfactories.ScriptFileReaderFactory;

/**
 * In this sample, a script file is loaded. The script file contains a set of requests to execute.
 */

public class ScriptFile {

    public static void main(String[] args) {
        AtExpose.create()
                .start(getScriptFileLoader(), true);
    }

    private static IDispatcher getScriptFileLoader(){
        return ScriptFileReaderFactory.create("script_file/scriptfile.txt");
    }
}
