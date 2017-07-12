package com.atexpose.api;

import io.schinzel.basicutils.collections.namedvalues.INamedValue;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Schinzel
 */
@RequiredArgsConstructor
public class Alias implements INamedValue, IStateNode {
    @Getter private final String name;
    @Getter @Setter private MethodObject method;


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", this.getName())
                .build();
    }
}
