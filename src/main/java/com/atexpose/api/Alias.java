package com.atexpose.api;

import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Schinzel
 */
@RequiredArgsConstructor
public class Alias implements IValueWithKey, IStateNode {
    @Getter private final String key;
    @Getter @Setter private MethodObject method;


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", this.getKey())
                .build();
    }
}
