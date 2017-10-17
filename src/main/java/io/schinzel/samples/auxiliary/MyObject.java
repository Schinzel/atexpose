package io.schinzel.samples.auxiliary;

import com.atexpose.Expose;
import io.schinzel.basicutils.Sandman;

/**
 * The purpose of this class is to hold sample method that will be exposed.
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


    @Expose(
            requiredAccessLevel = 1,
            arguments = {"Int"}
    )
    String doHeavyBackgroundJob(int count) {
        Sandman.snoozeSeconds(1);
        return "Phew, all done with heavy job " + count;
    }

}
