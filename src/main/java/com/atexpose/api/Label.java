package com.atexpose.api;

import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Schinzel
 */


@Accessors(prefix = "m")
@RequiredArgsConstructor
public class Label implements IValueWithKey, IStateNode {
    @Getter private final String mKey;
    private final String mDescription;
    @Getter private List<MethodObject> mMethods = new ArrayList<>(100);


    void addMethod(MethodObject mo) {
        this.getMethods().add(mo);
        Collections.sort(this.getMethods());
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", mKey)
                .add("Description", mDescription)
                .build();
    }

}
