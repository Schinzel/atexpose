package com.atexpose.dispatcher;

import com.atexpose.api.API;
import com.atexpose.dispatcher.logging.Logger;
import io.schinzel.basicutils.collections.valueswithkeys.IValueWithKey;
import io.schinzel.basicutils.state.IStateNode;

public interface IDispatcher extends IValueWithKey, IStateNode {

    void shutdown();


    IDispatcher commenceMessaging(API api);


    IDispatcher addLogger(Logger logger);


    IDispatcher removeLoggers();
}
