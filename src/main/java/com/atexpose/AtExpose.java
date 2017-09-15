package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.atexpose.*;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.util.DateTimeStrings;
import com.atexpose.util.mail.IEmailSender;
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
public class AtExpose implements IStateNode, IAtExposeCLI<AtExpose>, IAtExposeReports<AtExpose>,
        IAtExposeScriptFile<AtExpose>, IAtExposeTasks<AtExpose>, IAtExposeLog<AtExpose>,
        IAtExposeSqs<AtExpose> {
    /** Instance creation time. For status and debug purposes. */
    private final String mInstanceStartTime = DateTimeStrings.getDateTimeUTC();
    /** Reference to the API. */
    @Getter private final API mAPI;
    /** Holds an email sender instance if such has been set up. */
    @Getter private IEmailSender mMailSender;
    /** Holds the running dispatchers */
    @Getter ValuesWithKeys<Dispatcher> mDispatchers = ValuesWithKeys.create("Dispatchers");
    /** Hold the SQS senders added to this instance. */
    @Getter ValuesWithKeys<QueueProducerWrapper> mQueueProducers = ValuesWithKeys.create("QueueProducers");


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
     * @return A web server builder.
     */
    public WebServerBuilder getWebServerBuilder() {
        return new WebServerBuilder(this.getAPI(), this.getDispatchers());
    }


    /**
     * @return A SQS consumer builder
     */
    public DispatcherBuilder.SqsConsumerBuilder getSqsConsumerBuilder() {
        return new DispatcherBuilder(this.getAPI(), this.getDispatchers()).sqsConsumerBuilder();
    }


    /**
     * Shuts down all dispatchers of this instance.
     *
     * @return This for chaining.
     */
    public synchronized AtExpose shutdown() {
        //Shutdown all the dispatchers
        this.getDispatchers().forEach(Dispatcher::shutdown);
        //Empty the dispatcher collection.
        this.getDispatchers().clear();
        return this;
    }


    @Override
    public AtExpose getThis() {
        return this;
    }


    @Override
    public AtExpose setMailSender(IEmailSender emailSender) {
        mMailSender = emailSender;
        return this;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("TimeNow", DateTimeStrings.getDateTimeUTC())
                .add("StartTime", mInstanceStartTime)
                .addChild("EmailSender", this.getMailSender())
                .addChildren(this.getDispatchers())
                .addChildren(this.getQueueProducers())
                .build();
    }


}
