package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.api.Argument;
import com.atexpose.api.MethodObject;
import com.atexpose.api.data_types.AbstractDataType;
import com.atexpose.api.data_types.ClassDT;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.CliFactory;
import com.atexpose.dispatcher_factories.ScriptFileReaderFactory;
import com.atexpose.dispatcher_factories.WebServerBuilder;
import com.atexpose.generator.IGenerator;
import com.atexpose.generator.JsClientGenerator;
import com.atexpose.util.DateTimeStrings;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.valueswithkeys.ValuesWithKeys;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The central class that is the spider in the web.
 * <p>
 * The core purpose of an instance of this class is to start and stop dispatchers.
 *
 * @author Schinzel
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "UnusedReturnValue", "rawtypes", "unused", "RedundantSuppression"})
@Accessors(prefix = "m")
public class AtExpose implements IStateNode {
    /** Instance creation time. For status and debug purposes. */
    private final String mInstanceStartTime = DateTimeStrings.getDateTimeUTC();
    /** Reference to the API. */
    @Getter private final API mAPI;
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
        this.getAPI().expose(NativeMethods.create(this));
    }


    public AtExpose expose(Object object) {
        mAPI.expose(object);
        return this;
    }


    public AtExpose expose(Class<?> clazz) {
        mAPI.expose(clazz);
        return this;
    }

    public AtExpose generateJavaScriptClient(String fileName) {
        this.generate(new JsClientGenerator(fileName));
        return this;
    }

    public AtExpose generate(IGenerator generator) {
        List<MethodObject> methodObjects = new ArrayList<>(mAPI.getMethods().values());
        List<Class<?>> customClasses = getCustomClasses();
        generator.generate(methodObjects, customClasses);
        return this;
    }


    /**
     * @return Classes that exists outside atexpose and have been added to the api
     */
    private List<Class<?>> getCustomClasses() {
        return mAPI.getDataTypes()
                .stream()
                .filter(n -> n instanceof ClassDT)
                .map(n -> (Class<?>)((ClassDT) n).getClazz())
                .collect(Collectors.toList());
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
     * @param dispatcherName The dispatcher to shutdown.
     * @return This for chaining
     */
    public AtExpose closeDispatcher(String dispatcherName) {
        this.getDispatchers().get(dispatcherName).shutdown();
        this.getDispatchers().remove(dispatcherName);
        return this;
    }


    /**
     * Starts a command line interface
     *
     * @return This for chaining
     */
    public AtExpose startCLI() {
        this.start(CliFactory.create());
        return this;
    }

    public AtExpose readScriptFile(String fileName) {
        this.start(ScriptFileReaderFactory.create(fileName));
        return this;
    }

    /**
     * Starts a web server on port 5555
     *
     * @param webServerDir Path to web server dir. Relative to the
     *                     resource dir. For example "web" if web is
     *                     a dir in the resource dir
     * @return This for chaining
     */
    public AtExpose startWebServer(String webServerDir) {
        return this.startWebServer(webServerDir, 5555);
    }


    /**
     * Starts a web server
     *
     * @param webServerDir Path to web server dir. Relative to the
     *                     resource dir. For example "web" if web is
     *                     a dir in the resource dir
     * @param port         Port to start web server on
     * @return This for chaining
     */
    public AtExpose startWebServer(String webServerDir, int port) {
        IDispatcher webServer = WebServerBuilder.create()
                .webServerDir(webServerDir)
                .port(port)
                .build();
        this.start(webServer, false);
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
            dispatchers.forEach(this::start);
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

    //------------------------------------------------------------------------
    // API METHODS
    //------------------------------------------------------------------------

    public AtExpose addDataType(Class<?> clazz) {
        this.getAPI().addDataType(new ClassDT<>(clazz));
        return this;
    }

    public AtExpose addDataType(AbstractDataType dataType) {
        this.getAPI().addDataType(dataType);
        return this;
    }

    public AtExpose addArgument(Argument arg) {
        this.getAPI().addArgument(arg);
        return this;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("TimeNow", DateTimeStrings.getDateTimeUTC())
                .add("StartTime", mInstanceStartTime)
                .addChildren(this.getDispatchers())
                .build();
    }


}
