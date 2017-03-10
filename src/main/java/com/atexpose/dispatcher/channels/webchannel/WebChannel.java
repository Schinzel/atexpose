package com.atexpose.dispatcher.channels.webchannel;

import com.atexpose.dispatcher.channels.AbstractChannel;
import com.atexpose.util.ByteStorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.Thrower;

/**
 * The purpose of this class is to listen for and read incoming request on
 * a certain port and write responses to the requests.
 *
 * @author Schinzel
 */
public class WebChannel extends AbstractChannel {
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65535;
    private static final int MAX_PENDING_REQUESTS_DEFAULT = 50;
    private static final int TIMEOUT_MIN = 50;
    private static final int TIMEOUT_MAX = 30000;

    /**
     * The server socket. Shared by all threads listening to the same port.
     */
    private ServerSocket mServerSocket;
    /**
     * The client socket connection.
     */
    private Socket mClientSocket;
    /**
     * The socket timeout.
     */
    private int mSocketTimeout;
    /**
     * The maximum number of requests queued.
     */
    private int mMaxPendingRequests;
    /**
     * For logging and statistics, hold the time it took to read the message
     * from first to last byte.
     */
    private long mLogRequestReadTime;


    //------------------------------------------------------------------------
    // CONSTRUCTORS AND SHUTDOWN
    //------------------------------------------------------------------------
    public WebChannel(int port, int timeout) {
        Thrower.throwIfOutsideRange(port, "port", PORT_MIN, PORT_MAX);
        Thrower.throwIfOutsideRange(timeout, "timeout", TIMEOUT_MIN, TIMEOUT_MAX);
        mSocketTimeout = timeout;
        mMaxPendingRequests = MAX_PENDING_REQUESTS_DEFAULT;
        try {
            mServerSocket = new ServerSocket(port, mMaxPendingRequests);
        } catch (IOException ioe) {
            throw new RuntimeException("Error starting thread on port " + port
                    + ". Most likely the port is busy. " + ioe.getMessage());
        }
    }


    private WebChannel(ServerSocket serverSocket, int iTimeout) {
        mServerSocket = serverSocket;
        mSocketTimeout = iTimeout;
    }


    @Override
    public AbstractChannel getClone() {
        return new WebChannel(mServerSocket, mSocketTimeout);
    }


    @Override
    public void shutdown(Thread thread) {
        try {
            mServerSocket.close();
        } catch (IOException ex) {
            System.out.println("Error while closing socket");
        }
    }


    //------------------------------------------------------------------------
    // MESSAGING
    //------------------------------------------------------------------------
    @Override
    public boolean getRequest(ByteStorage request) {
        boolean booTimeoutError;
        do {
            booTimeoutError = false;
            try {
                mClientSocket = mServerSocket.accept();
            } catch (SocketException se) {
                //If socket is closed, most likely the shutdown method was used.
                if (mServerSocket.isClosed()) {
                    return false;
                }
            } catch (IOException ioe) {
                //If an error was thrown while waiting for accept the shutdown method in this object was probably(hopefully) invoked.
                return false;
            }
            try {
                mLogRequestReadTime = System.currentTimeMillis();
                mClientSocket.setSoTimeout(mSocketTimeout);
                SocketRW.read(request, mClientSocket);
            }//Catch read timeout errors
            catch (java.io.InterruptedIOException iioe) {
                booTimeoutError = true;
                mLogRequestReadTime = System.currentTimeMillis() - mLogRequestReadTime;
                String err = "Server got read timeout error when reading from client socket No of bytes: " + request.getNoOfBytesStored() + " ";
                try {
                    mClientSocket.close();
                } catch (IOException e) {
                    err += " and failed to close the connection";
                }
                err += " : " + iioe.getMessage();
                throw new RuntimeException(err);
            } catch (Exception e) {
                mLogRequestReadTime = System.currentTimeMillis() - mLogRequestReadTime;
                throw new RuntimeException("Error while reading from socket. " + e.getMessage());
            }
        } while (booTimeoutError);
        mLogRequestReadTime = System.currentTimeMillis() - mLogRequestReadTime;
        return true;
    }


    @Override
    public void writeResponse(byte[] response) {
        try {
            //Send the Response to the client.
            SocketRW.write(mClientSocket, response);
        } catch (IOException ioe) {
            //If not "Error while writing to socket Connection reset by peer: socket write error"
            //Error indicating timeout on client.
            if (ioe.getMessage().compareToIgnoreCase(
                    "Error while writing to socket Connection reset by peer: socket write error") == -1) {
                throw new RuntimeException("Error while writing to socket " + ioe.getMessage());
            }
        } finally {
            //Close the client connection.
            try {
                mClientSocket.close();
            } catch (IOException e) {
            }
        }
    }


    //------------------------------------------------------------------------
    // LOGGING & STATS
    //------------------------------------------------------------------------
    @Override
    public long requestReadTime() {
        return mLogRequestReadTime;
    }


    @Override
    public String senderInfo() {
        return mClientSocket.getInetAddress().getHostAddress() + ":" + mClientSocket.getPort();
    }
    //------------------------------------------------------------------------
    // STATUS
    //------------------------------------------------------------------------


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Port", mServerSocket.getLocalPort())
                .add("Timeout", mSocketTimeout)
                .add("Queue", mMaxPendingRequests)
                .build();
    }
}

