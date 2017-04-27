package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.state.State;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Schinzel
 */
@Accessors(prefix = "m")
@AllArgsConstructor
public class ParserTestClass implements IParser {
    @Setter Request mRequest;


    @Override
    public Request getRequest(String incomingRequest) {
        return mRequest;
    }


    @Override
    public IParser getClone() {
        return new ParserTestClass(mRequest);
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("ClassName", this.getClass().getSimpleName())
                .build();
    }

}

