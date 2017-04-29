package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.state.IStateNode;

/**
 * The purpose of this interface is to parse a raw incoming request - for example an
 * http request - and return a Request object.
 * <p>
 * Created by schinzel on 2017-04-25.
 */
public interface IParser extends IStateNode {

    Request getRequest(String incomingRequest);

    IParser getClone();
}
