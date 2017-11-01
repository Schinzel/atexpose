package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.util.DateTimeStrings;
import com.atexpose.util.mail.IEmailSender;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.valueswithkeys.ValuesWithKeys;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The central class that is the spider in the web.
 * <p>
 * The core purpose of an instance of this class is to start and stop dispatchers.
 *
 * @author Schinzel
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue", "UnusedReturnValue"})
@Accessors(prefix = "m")
public class AtExpose implements IStateNode {
    /** Instance creation time. For status and debug purposes. */
    private final String mInstanceStartTime = DateTimeStrings.getDateTimeUTC();
    /** Reference to the API. */
    @Getter private final API mAPI;
    /** Holds an email sender instance if such has been set up. */
    @Getter private IEmailSender mMailSender;
    /** Holds the running dispatchers */
    @Getter ValuesWithKeys<IDispatcher> mDispatchers = ValuesWithKeys.create("Dispatchers");


    /**
     * @return A freshly created instance.
     */
    public static AtExpose create() {
        return new AtExpose();
    }


    AtExpose() {
        mAPI = new API();
        NativeSetup.setUp(this.getAPI());
        this.getAPI().expose(ExposedAtExpose.create(this));
    }


    public AtExpose expose(Object object) {
        mAPI.expose(object);
        return this;
    }


    public AtExpose expose(Class clazz) {
        mAPI.expose(clazz);
        return this;
    }


    /**
     * Shuts down all dispatchers of this instance.
     *
     * @return This for chaining.
     */
    public synchronized AtExpose shutdown() {
        //Shutdown all the dispatchers
        this.getDispatchers().forEach(IDispatcher::shutdown);
        //Empty the dispatcher collection.
        this.getDispatchers().clear();
        return this;
    }


    /**
     * @param dispatcherName The name of the dispatcher to return
     * @return The dispatcher with the argument name
     */
    public IDispatcher getDispatcher(String dispatcherName) {
        return this.getDispatchers().get(dispatcherName);
    }


    /**
     * @param dispatcherName The dispatcher to shutdown.
     * @return This for chaining
     */
    public AtExpose closeDispatcher(String dispatcherName) {
        this.getDispatchers().get(dispatcherName).shutdown();
        this.getDispatchers().remove(dispatcherName);
        return this;
    }


    /**
     * Central method for starting a dispatcher
     *
     * @param dispatcher The dispatcher to start
     * @return This for chaining
     */
    public AtExpose start(IDispatcher dispatcher) {
        return this.start(dispatcher, false);
    }


    /**
     * Starts a collection of dispatchers
     *
     * @param dispatchers The dispatchers to start
     * @return This for chaining
     */
    public AtExpose start(Iterable<IDispatcher> dispatchers) {
        if (Checker.isNotEmpty(dispatchers)) {
            dispatchers.forEach(d -> this.start(d));
        }
        return this;
    }


    /**
     * Central method for starting a dispatcher
     *
     * @param dispatcher       The dispatcher to start
     * @param oneOffDispatcher If true the dispatcher is a one-off that executes and then
     *                         terminates. Is never added to the dispatcher collection.
     * @return This for chaining
     */
    public AtExpose start(IDispatcher dispatcher, boolean oneOffDispatcher) {
        //If this is not a temporary dispatcher, i.e. a dispatcher that dies once it has read its requests and delivered its responses
        if (!oneOffDispatcher) {
            //Add the newly created dispatcher to the dispatcher collection
            this.getDispatchers().add(dispatcher);
        }
        //Start the messaging!
        dispatcher.commenceMessaging(this.getAPI());
        return this;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("TimeNow", DateTimeStrings.getDateTimeUTC())
                .add("StartTime", mInstanceStartTime)
                .addChild("EmailSender", this.getMailSender())
                .addChildren(this.getDispatchers())
                .build();
    }


}
