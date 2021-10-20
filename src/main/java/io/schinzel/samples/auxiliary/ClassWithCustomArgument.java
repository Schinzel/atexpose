package io.schinzel.samples.auxiliary;

import com.atexpose.Expose;

public class ClassWithCustomArgument {
    int mPrice = 17;


    @Expose(
            description = "The price is set to the argument value",
            arguments = {"Price"},
            theReturn = "The argument string with a prefix"
    )
    public String setPrice(int price) {
        mPrice = price;
        return "It was set to '" + mPrice + "'.";
    }


    @Expose(
            theReturn = "The price that has been set"
    )
    public int getPrice() {
        return mPrice;
    }

}
