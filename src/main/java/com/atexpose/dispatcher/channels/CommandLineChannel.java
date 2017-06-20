package com.atexpose.dispatcher.channels;

import com.atexpose.MyProperties;
import com.atexpose.errors.RuntimeError;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.EncodingUtil;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Sandman;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.state.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * The purpose of this class is to read command from the command-line interface.
 *
 * @author Schinzel
 */
public class CommandLineChannel implements IChannel {
    private final InputStream mInputStream;
    private boolean mIsToShutdown = false;
    private static final String CLI_START_OF_LINE = "[@Expose]>";
    //------------------------------------------------------------------------
    // CONSTRUCTORS AND SHUTDOWN
    //------------------------------------------------------------------------


    public CommandLineChannel() {
        this(System.in);
    }


    private CommandLineChannel(InputStream customInputStream) {
        mInputStream = customInputStream;
    }


    @Override
    public IChannel getClone() {
        throw new RuntimeError("The command line interface cannot be multi-threaded.");
    }


    @Override
    public void shutdown(Thread thread) {
        mIsToShutdown = true;
    }
    //------------------------------------------------------------------------
    // MESSAGING
    //------------------------------------------------------------------------


    @Override
    public boolean getRequest(ByteStorage request) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream, Charset.forName("UTF-8")));
            String input;
            do {
                System.out.print(CLI_START_OF_LINE);
                while (!reader.ready()) {
                    Sandman.snoozeMillis(20);
                    if (mIsToShutdown) {
                        return false;
                    }
                }
                input = reader.readLine();
                //convert the read line to bytes and add to request
                request.add(EncodingUtil.convertToByteArray(input));
            }//Loop while the input is empty 
            while (Checker.isEmpty(input));
        } catch (IOException ex) {
            throw new RuntimeError("Got an io exception in the command line interface. " + ex.getMessage());
        }
        return true;
    }


    @Override
    public void writeResponse(byte[] response) {
        if (response != null && response.length > 0) {
            try {
                System.out.write(response);
                System.out.write(UTF8.getBytes(MyProperties.OS_LINE_SEPARATOR));
            } catch (IOException e) {
                throw new RuntimeError("Error while writing to prompt.");
            }
        }
    }


    //------------------------------------------------------------------------
    // LOGGING & STATS
    //------------------------------------------------------------------------
    @Override
    public long responseWriteTime() {
        return 0;
    }


    @Override
    public String senderInfo() {
        return "CLI";
    }


    @Override
    public long requestReadTime() {
        return -1;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Tag", CLI_START_OF_LINE)
                .build();
    }


}
