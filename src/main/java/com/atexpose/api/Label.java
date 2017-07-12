package com.atexpose.api;

import io.schinzel.basicutils.collections.namedvalues.INamedValue;
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
public class Label implements INamedValue, IStateNode {
    @Getter private final String mName;
    private final String mDescription;
    @Getter private List<MethodObject> mMethods = new ArrayList<>(100);


    void addMethod(MethodObject mo) {
        this.getMethods().add(mo);
        Collections.sort(this.getMethods());
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Name", mName)
                .add("Description", mDescription)
                .build();
    }

}
