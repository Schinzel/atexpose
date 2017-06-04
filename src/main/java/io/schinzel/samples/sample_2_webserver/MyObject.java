package io.schinzel.samples.sample_2_webserver;

import com.atexpose.Expose;

/**
 * The purpose of this class
 * <p>
 * Created by Schinzel on 2017-03-06.
 */
public class MyObject {
    String mStr = "bapp";


    @Expose(
            description = "The thing is set to the argument value",
            arguments = {"String"},
            theReturn = "The argument string with a prefix"
    )
    public String setTheThing(String str) {
        mStr = str;
        return "It was set to '" + str + "'.";
    }


    @Expose(
            theReturn = "The value the thing has been set to."
    )
    public String getTheThing() {
        return mStr;
    }

}
