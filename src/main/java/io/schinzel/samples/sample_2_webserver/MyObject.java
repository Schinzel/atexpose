package io.schinzel.samples.sample_2_webserver;

import com.atexpose.Expose;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-03-06.
 */
public class MyObject {
    public String mStr = "bapp";


    @Expose(
            arguments = {"String"}
    )
    public String setIt(String str) {
        mStr = str;
        return "It was set to '" + str + "'.";
    }

    @Expose
    public String getIt(){
        return mStr;
    }

}
