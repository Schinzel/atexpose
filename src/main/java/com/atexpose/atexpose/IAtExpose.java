package com.atexpose.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.IDispatcher;
import io.schinzel.basicutils.collections.valueswithkeys.ValuesWithKeys;

/**
 * This is the base interface.
 * <p>
 * Actual functionality are implemented in a set of interfaces that extends this
 * interface. The implementing class implements one or several of the interfaces that extended this.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
interface IAtExpose<T extends IAtExpose<T>> {


    /**
     * @return The API used by this instance.
     */
    API getAPI();


    /**
     * @return The collection with all dispatchers.
     */
    ValuesWithKeys<IDispatcher> getDispatchers();


    /**
     * Exists for programming technical reasons only. Allows implementing classes and extending
     * interfaces to return
     * a "this" that refers to implementing class or extending interface, instead of the interface
     * being implemented
     * or extended. This as opposed to casting and/or overloading methods just to return the correct
     * type.
     * <p>
     * This should really be package private or protected. But as this is not an option, it has to
     * be public.
     *
     * @return This for chaining.
     */
    T getThis();


    /**
     *
     * @param dispatcherName The name of the dispatcher to return.
     * @return The dispatcher with the argument name.
     */
    default IDispatcher getDispatcher(String dispatcherName) {
        return this.getDispatchers().get(dispatcherName);
    }


    /**
     * @param dispatcherName The dispatcher to shutdown.
     * @return This for chaining.
     */
    default T closeDispatcher(String dispatcherName) {
        this.getDispatchers().get(dispatcherName).shutdown();
        this.getDispatchers().remove(dispatcherName);
        return this.getThis();
    }


    /**
     * Central method for starting a dispatcher.
     *
     * @param dispatcher The dispatcher to start
     * @return The dispatcher that was just started.
     */
    default T startDispatcher(IDispatcher dispatcher) {
        return this.startDispatcher(dispatcher, false);
    }


    /**
     * Central method for starting a dispatcher.
     *
     * @param dispatcher       The dispatcher to start
     * @param oneOffDispatcher If true the dispatcher is a one-off that executes and then
     *                         terminates. Is never added
     *                         to the dispatcher collection.
     * @return The dispatcher that was just started.
     */
    default T startDispatcher(IDispatcher dispatcher, boolean oneOffDispatcher) {
        //If this is not a temporary dispatcher, i.e. a dispatcher that dies once it has read its requests and delivered its responses
        if (!oneOffDispatcher) {
            //Add the newly created dispatcher to the dispatcher collection
            this.getDispatchers().add(dispatcher);
        }
        //Start the messaging!
        dispatcher.commenceMessaging(this.getAPI());
        return this.getThis();
    }

}
