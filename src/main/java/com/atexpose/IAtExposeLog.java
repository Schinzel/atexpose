package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;

/**
 * The purpose of this interface is to handle @Expose logs.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
public interface IAtExposeLog<T extends IAtExpose<T>> extends IAtExpose<T> {

    /**
     * Removes all loggers from a dispatcher.
     *
     * @param dispatcherName The name of the dispatchers from which all loggers - event and error - will be removed.
     * @return This for chaining.
     */
    default T removeAllLoggers(String dispatcherName) {
        if (this.getDispatchers().has(dispatcherName)) {
            Dispatcher dispatcher = this.getDispatchers().get(dispatcherName);
            dispatcher.removeLoggers();
            return this.getThis();
        } else {
            throw new RuntimeException("No such dispatcher '" + dispatcherName + "'");
        }
    }
}
