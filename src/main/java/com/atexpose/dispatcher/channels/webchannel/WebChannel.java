package com.atexpose.dispatcher.channels.webchannel;

import com.atexpose.dispatcher.channels.AbstractChannel;
import com.atexpose.dispatcher.channels.webchannel.http.HttpsRedirect;
import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.EncodingUtil;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The purpose of this class is to listen for and read incoming request on
 * a certain port and write responses to the requests.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class WebChannel extends AbstractChannel {
    private static final int MAX_PENDING_REQUESTS = 50;
    /** The server socket. Shared by all threads listening to the same port. */
    final private ServerSocket mServerSocket;
    /** The socket timeout. */
    final private int mSocketTimeout;
    /** If true, http requests are redirected to https requests. */
    @Getter(AccessLevel.PACKAGE) private final boolean mToForceHttps;
    /** For logging and statistics, hold the time it took to read the message from first to last byte. */
    private long mLogRequestReadTime;
    /** The client socket connection. */
    private Socket mClientSocket;


    //------------------------------------------------------------------------
    // CONSTRUCTORS AND SHUTDOWN
    //------------------------------------------------------------------------
    @Builder
    WebChannel(int port, int timeout, boolean forceHttps) {
        this(getServerSocket(port), timeout, forceHttps);
        Thrower.throwIfOutsideRange(port, "port", 1, 65535);
        Thrower.throwIfOutsideRange(timeout, "timeout", 50, 30000);
    }


    static ServerSocket getServerSocket(int port) {
        try {
            return new ServerSocket(port, MAX_PENDING_REQUESTS);
        } catch (IOException ioe) {
            throw new RuntimeException("Error starting thread on port " + port + ". Most likely the port is busy. " + ioe.getMessage());
        }
    }


    @Builder(builderMethodName = "cloneBuilder", buildMethodName = "buildClone")
    private WebChannel(ServerSocket serverSocket, int timeout, boolean forceHttps) {
        mServerSocket = serverSocket;
        mSocketTimeout = timeout;
        mToForceHttps = forceHttps;
    }


    @Override
    public AbstractChannel getClone() {
        return WebChannel.cloneBuilder()
                .timeout(mSocketTimeout)
                .serverSocket(mServerSocket)
                .forceHttps(this.isToForceHttps())
                .buildClone();
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
        boolean keepReadingFromSocket;
        do {
            keepReadingFromSocket = false;
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
                HttpRequest httpRequest = SocketRW.read(request, mClientSocket);
                if (httpRequest.isGhostCall()){

                }
                //If is to force https and this is an http-request
                if (this.isToForceHttps() && HttpsRedirect.isHttpReuqest(httpRequest)) {
                    //Get the full url with https
                    String urlWithHttps = HttpsRedirect.getUrlWithHttps(
                            httpRequest.getRequestHeaderValue("Host"),
                            httpRequest.getURL());
                    //Get redirect header
                    String redirectHeader = HttpsRedirect.wrapRedirect(urlWithHttps, RedirectHttpStatus.TEMPORARY);
                    //Covert the redirect header to UTF-8 byte array
                    byte[] redirectAsByteArr = EncodingUtil.convertToByteArray(redirectHeader);
                    //Send the redirect instruction to client
                    this.writeResponse(redirectAsByteArr);
                    //Clear the incoming request.
                    request.clear();
                    keepReadingFromSocket = true;
                }
            }//Catch read timeout errors
            catch (InterruptedIOException iioe) {
                keepReadingFromSocket = true;
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
        } while (keepReadingFromSocket);
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
                .add("Queue", MAX_PENDING_REQUESTS)
                .add("ForceHttps", this.isToForceHttps())
                .build();
    }
}

