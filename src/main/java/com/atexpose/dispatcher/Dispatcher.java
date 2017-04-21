package com.atexpose.dispatcher;

import com.atexpose.api.API;
import com.atexpose.api.MethodObject;
import com.atexpose.dispatcher.channels.AbstractChannel;
import com.atexpose.dispatcher.logging.LogEntry;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import com.atexpose.dispatcher.wrapper.IWrapper;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.EncodingUtil;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.collections.keyvalues.IValueKey;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The dispatcher is the central cog wheel.
 * <p>
 * From the assigned channel the dispatcher pulls an incoming messages.
 * The parser extracts method name and argument values from the incoming message.
 * The incoming request is executed.
 * The response is wrapped with the assigned wrapper.
 * The wrapped response is sent with the assigned channel.
 * <p>
 * The log data is sent to the assigned loggers (zero, one or many).
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class Dispatcher implements Runnable, IValueKey, IStateNode {
    /** The key as part of the IStateNode interface. Is the name of the Dispatchers. */
    @Getter final String mKey;
    /** The API that is exposed. */
    private final API mAPI;
    /** Receives incoming messages and sends wrapped responses. */
    private final AbstractChannel mChannel;
    /** Parses the incoming messages. */
    private final AbstractParser mParser;
    /** Wraps the responses to send. */
    private final IWrapper mWrapper;
    /**
     * The access level of this dispatcher. The dispatcher can invoke methods with the same access
     * level or lower.
     **/
    private final int mAccessLevel;
    /** Says which thread this object is. Useful for debugging and diagnostics. */
    private final int mThreadNumber;
    /** The thread that this dispatcher executes in. */
    private Thread mThread;
    /** The loggers assigned to this dispatcher. */
    private List<Logger> mLoggers = new ArrayList<>();
    /**
     * Contains a reference to the next dispatcher if this dispatcher is
     * multi-threaded. Multi-threaded dispatchers are stored as a linked-list.
     * This variable is a reference to the next dispatcher in the list. If it is
     * null, it is the last in the list or the it is single-threaded.
     */
    private Dispatcher mNextDispatcher;
    // ------------------------------------
    // - CONSTRUCTOR
    // ------------------------------------


    /**
     * Sets up a dispatcher.
     */
    @Builder
    private Dispatcher(String name, int noOfThreads, int accessLevel, AbstractChannel channel, AbstractParser parser, IWrapper wrapper, API api) {
        mKey = name;
        Thrower.throwIfTooSmall(noOfThreads, "noOfThreads", 1);
        Thrower.throwIfEmpty(name, "name");
        Thrower.throwIfOutsideRange(accessLevel, "accessLevel", 1, 3);
        mThreadNumber = noOfThreads;
        mAccessLevel = accessLevel;
        mChannel = channel;
        mParser = parser;
        mWrapper = wrapper;
        mAPI = api;
        //If more than one dispatcher to set up
        if (mThreadNumber > 1) {
            //Set up the next dispatcher
            mNextDispatcher = Dispatcher.builder()
                    .name(this.getKey())
                    .accessLevel(mAccessLevel)
                    .channel(mChannel.getClone())
                    .parser(mParser.getClone())
                    .wrapper(mWrapper)
                    .noOfThreads(mThreadNumber - 1)
                    .api(mAPI)
                    .build();
        }
    }
    // ------------------------------------
    // - START & STOP
    // ------------------------------------


    /**
     * Starts the messaging and tells the next dispatcher to start its messaging recursively until
     * all dispatchers of
     * that a siblings to this have been started.
     *
     * @param isSynchronized If true, the dispatcher is to run in the invoking thread. If false, the
     *                       dispatcher
     *                       will start a new thread and execute in this.
     */
    public void commenceMessaging(boolean isSynchronized) {
        //If there is a next dispatcher
        if (mNextDispatcher != null) {
            //Tell the next dispatcher to start its messaging.
            mNextDispatcher.commenceMessaging(isSynchronized);
        }
        //If this is a synchronized execution
        if (isSynchronized) {
            //Run in the requesting thread.
            this.run();
        }//Else, i.e. should run in a new separate thread.
        else {
            //Start a new thread and let this dispatcher execute in this thread.
            mThread = new Thread(this);
            mThread.setName(this.getKey() + ":" + mThreadNumber);
            mThread.start();
        }
    }


    /**
     * Shutdown this and the next dispatcher.
     */
    public void shutdown() {
        //If there was a next dispatchers
        if (this.mNextDispatcher != null) {
            //Tell the next dispatcher to shutdown
            this.mNextDispatcher.shutdown();
        }
        //Tell the channel to shut down. Send this thread as it is required for some channels to interrupt listening.
        this.mChannel.shutdown(mThread);
    }


    // ------------------------------------
    // - REQUEST HANDLING
    // ------------------------------------
    @Override
    public void run() {
        ByteStorage incomingRequest = new ByteStorage();
        String decodedIncomingRequest;
        Object responseAsObjects;
        Object responseAsStrings;
        String wrappedResponse;
        byte[] wrappedResponseAsUtf8ByteArray;
        LogEntry logEntry = new LogEntry(mThreadNumber, mChannel, mParser);
        while (true) {
            try {
                if (!mChannel.getRequest(incomingRequest)) {
                    break;
                }
                logEntry.setTimeOfIncomingCall();
                // Get incoming request as string.
                decodedIncomingRequest = incomingRequest.getAsString();
                //Send the incoming request to the protocol for extracting method name and arguments
                mParser.parseRequest(decodedIncomingRequest);
                // If this request should be redirected
                if (mParser.isRedirect()) {
                    Pair<String, RedirectHttpStatus> redirect = mParser.getRedirect();
                    wrappedResponse = mWrapper.wrapRedirect(redirect.getLeft(), redirect.getRight());
                    wrappedResponseAsUtf8ByteArray = EncodingUtil.convertToByteArray(wrappedResponse);
                } //else if is a file request
                else if (mParser.isFileRequest()) {
                    wrappedResponse = EmptyObjects.EMPTY_STRING;
                    wrappedResponseAsUtf8ByteArray = mWrapper.wrapFile(mParser.getFileName());
                } // Else must be a method call 
                else {
                    MethodObject methodObject = mAPI.getMethodObject(mParser.getMethodName());
                    // is the dispatcher authorized to access this method
                    checkAccessLevel(methodObject.getAccessLevelRequiredToUseThisMethod());
                    responseAsObjects = methodObject.invoke(mParser.getArgumentValues(),
                            mParser.getArgumentNames(), mAccessLevel);
                    //If return type is Json
                    if (methodObject.getReturnDataType().isJson()) {
                        //Do json wrapping
                        wrappedResponse = mWrapper.wrapJSON((JSONObject) responseAsObjects);
                    } else {
                        responseAsStrings = methodObject.getReturnDataType().convertFromDataTypeToString(responseAsObjects);
                        wrappedResponse = mWrapper.wrapResponse((String) responseAsStrings);
                    }
                    wrappedResponseAsUtf8ByteArray = EncodingUtil.convertToByteArray(wrappedResponse);
                }
            } catch (Exception e) {
                logEntry.setTimeOfIncomingCall();
                logEntry.setIsError();
                // Get incoming request as string.
                decodedIncomingRequest = incomingRequest.getAsString();
                wrappedResponse = mWrapper.wrapError(e.getMessage());
                wrappedResponseAsUtf8ByteArray = EncodingUtil.convertToByteArray(wrappedResponse);
            }
            mChannel.writeResponseExternal(wrappedResponseAsUtf8ByteArray);
            logEntry.setLogData(decodedIncomingRequest, wrappedResponse);
            this.log(logEntry);
            logEntry.cleanUpLogData();
            incomingRequest.clear();
            mParser.clear();
        }
    }


    private void checkAccessLevel(int methodAccessLevel) {
        if (methodAccessLevel > this.mAccessLevel) {
            throw new RuntimeException("Cannot access the requested method through this dispatcher. Method requires access level "
                    + methodAccessLevel + " and the used dispatcher only has access level " + this.mAccessLevel + ".");
        }
    }
    // ------------------------------------
    // - Logger
    // ------------------------------------


    /**
     * Adds a logger to this dispatcher.
     *
     * @param logger The logger to add.
     * @return This for chaining
     */
    public Dispatcher addLogger(Logger logger) {
        mLoggers.add(logger);
        if (mNextDispatcher != null) {
            mNextDispatcher.addLogger(logger);
        }
        return this;
    }


    /**
     * Removes all loggers from this dispatcher.
     *
     * @return This for chaining.
     */
    public Dispatcher removeLoggers() {
        mLoggers = EmptyObjects.EMPTY_LIST;
        if (mNextDispatcher != null) {
            mNextDispatcher.removeLoggers();
        }
        return this;
    }


    /**
     * Logs the argument log entry all attached (if any) loggers.
     *
     * @param logEntry The entry to add to logs.
     */
    private void log(LogEntry logEntry) {
        //Go through all logger attached to this dispatcher
        for (Logger logger : mLoggers) {
            //Log event
            logger.log(logEntry);
        }
    }
    // ------------------------------------
    // - State
    // ------------------------------------


    public State getState() {
        return State.getBuilder()
                .add("Name", this.getKey())
                .add("AccessLevel", mAccessLevel)
                .add("Threads", this.mThreadNumber)
                .addChild("Parser", mParser)
                .addChild("Wrapper", mWrapper)
                .addChild("Channel", mChannel)
                .build();
    }

}
