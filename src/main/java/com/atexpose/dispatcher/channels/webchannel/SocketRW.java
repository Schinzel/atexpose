package com.atexpose.dispatcher.channels.webchannel;

import com.atexpose.dispatcher.parser.urlparser.httprequest.HttpRequest;
import com.atexpose.util.ByteStorage;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Sandman;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


/**
 * The purpose of this class is to read from and write to sockets.
 * <p>
 * Created by Schinzel on 2017-03-08.
 */
class SocketRW {
    private static final int BUFFER_SIZE = 128;
    /** Snooze time between attempts to read laggard bytes */
    private static final int LAGGARD_SNOOZE_BETWEEN_READS = 10;
    /** Max number of times we try to read request body */
    private static final int LAGGARD_MAX_ATTEMPTS = 30;


    /**
     * Reads a request from a socket.
     *
     * @param request The incoming request is written to this argument.
     * @param socket  The socket to read from.
     */
    public static HttpRequest read(ByteStorage request, Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte firstByte = (byte) inputStream.read();
        request.add(firstByte);
        byte[] arr = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        //While there is more than one byte available and the read does not return end of stream
        while ((inputStream.available() > 0) && ((bytesRead = inputStream.read(arr)) != -1)) {
            request.add(arr, 0, bytesRead);
        }
        //Do a short snooze. This as if using a POST request to send data
        //all the bytes might not be available without this snooze
        Sandman.snoozeMillis(10);
        //Read again from the socket
        while ((inputStream.available() > 0) && ((bytesRead = inputStream.read(arr)) != -1)) {
            request.add(arr, 0, bytesRead);
        }
        //About one in a few thousand calls have a pause between header and body that
        //is not caught by above code
        return getLaggardBytes(request, inputStream);
    }


    /**
     * @param socket   The socket to write to.
     * @param response The message to write to the socket.
     */
    public static void write(Socket socket, byte[] response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        if (response == null) {
            response = new byte[0];
        }
        outputStream.write(response);
        outputStream.flush();
    }


    /**
     * Assumes the HTTP protocol. Handles the case that there is a pause in the sending of bytes to the argument
     * input stream. The method reads from the argument input stream and after the first pause in the receiving
     * of bytes parses the incoming message. If the HTTP header indicates that there should be more bytes than have
     * received keep trying until we got all bytes or we have reached the max number of attempts.
     *
     * @param request     Contains the partial message and it will contain the whole message.
     * @param inputStream The stream that receives the incoming message.
     */
    private static HttpRequest getLaggardBytes(ByteStorage request, InputStream inputStream) throws IOException {
        HttpRequest hr = null;
        if (request.getNoOfBytesStored() == 1) {
            hr = new HttpRequest(" ");
            //If there was any bytes
        } else if (request.getNoOfBytesStored() > 1) {
            hr = new HttpRequest(request.getAsString());
            String sContentLength = hr.getRequestHeaderValue("Content-Length");
            //If there was a content length
            if (!Checker.isEmpty(sContentLength)) {
                int iContentLength = Integer.parseInt(sContentLength);
                int attempts = 0;
                while ((hr.getBody().getBytes(Charset.forName("UTF-8")).length
                        < iContentLength) && (attempts < LAGGARD_MAX_ATTEMPTS)) {
                    attempts++;
                    Sandman.snoozeMillis(LAGGARD_SNOOZE_BETWEEN_READS);
                    int bytesRead = 0;
                    byte[] arr = new byte[BUFFER_SIZE];
                    //Read again from the socket
                    while ((inputStream.available() > 0) && ((bytesRead = inputStream.read(arr)) != -1)) {
                        //while ((bytesRead = inputStream.read(arr)) != -1) {
                        request.add(arr, 0, bytesRead);
                    }
                    hr = new HttpRequest(request.getAsString());
                }
            }
        }
        return hr;
    }

}