package io.schinzel.samples.sample_script_file;

import com.atexpose.AtExpose;

public class Main {

    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.loadScriptFile("sample_script_file/scriptfile.txt");
        //Start command line interface
        atExpose.startCLI();



    }
}
