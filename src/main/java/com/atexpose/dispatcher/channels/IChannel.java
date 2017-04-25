package com.atexpose.dispatcher.channels;

import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.state.IStateNode;

/**
 * The purpose of this abstract class is to implement a generic channel for
 * receiving messages and returning responses to those messages.
 *
 * @author Schinzel
 */
public interface IChannel extends IStateNode {


    /**
     * Returns a message received.
     * A false return indicates that no more messages will come through this
     * channel and that it should be closed. For example a file reader
     * should return false once the last byte has been read.
     *
     * @param request The message returned is stored in the argument variable.
     * @return True if had a message, else false.
     */
    boolean getRequest(ByteStorage request);


    /**
     * Writes a response.
     *
     * @param response The response to write.
     */
    void writeResponse(byte[] response);


    /**
     * @return The time it took to write a response in milliseconds.
     */
    long responseWriteTime();


    /**
     * Terminates the object.
     *
     * @param thread The thread that uses this channel. The thread can be
     *               required to interrupt processes.
     */
    void shutdown(Thread thread);


    /**
     * Returns a clone of itself. Is used with multi-threaded message handlers.
     * Each thread has its own communicator-object.
     * Should throw an exception if does not support multi-threading.
     *
     * @return A clone of itself.
     */
    IChannel getClone();


    /**
     * Returns the time from when the connection was made till when the last
     * byte was transferred.
     * Is allowed to return -1 if the information is deemed uninteresting.
     *
     * @return The read time of the latest message.
     */
    long requestReadTime();


    /**
     * Returns information on the sender of the message.
     * In the case of a socket reader, the sender info can be the IP-address and
     * port.
     * Is allowed to return null, if the sender information is trivial. For
     * example; in the case of a prompt reader
     * the sender is self-evident.
     *
     * @return Information on the sender of the message.
     */
    String senderInfo();

}
