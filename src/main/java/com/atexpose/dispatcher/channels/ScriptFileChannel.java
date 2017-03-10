package com.atexpose.dispatcher.channels;

import com.atexpose.MyProperties;
import com.atexpose.errors.RuntimeError;
import com.atexpose.util.ByteStorage;
import com.atexpose.util.EncodingUtil;
import com.atexpose.util.FileRW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.Checker;

/**
 * The purpose of this class is to read a text file containing one or several commands.
 *
 * @author Schinzel
 */
public class ScriptFileChannel extends AbstractChannel {
    /**
     * If a line starts with a comment qualifier, that line is ignored.
     */
    private static final String COMMENT_QUALIFIER = "#";
    /**
     * The name of the file to read.
     */
    private final String mFilename;
    /**
     * The time it took to read a line. For logging and statistics.
     */
    private long mLineReadTime;
    /**
     * Used to read the file.
     */
    private BufferedReader mBufferedReader;
    //------------------------------------------------------------------------
    // CONSTRUCTORS AND SHUTDOWN
    //------------------------------------------------------------------------


    /**
     * @param filename The name of the file to read
     */
    public ScriptFileChannel(String filename) {
        mFilename = filename;
        if (Checker.isEmpty(mFilename)) {
            throw new RuntimeError("Empty filename was given as argument.");
        }
        if (!FileRW.fileExists(filename)) {
            throw new RuntimeError("No such file or it cannot be read '" + filename + "'");
        }
        InputStream is = FileRW.getInputStream(filename);
        mBufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName(MyProperties.ENCODING)));
    }


    @Override
    public void shutdown(Thread thread) {
    }


    @Override
    public AbstractChannel getClone() {
        throw new RuntimeError("Script File Channels are single threaded.");
    }


    //------------------------------------------------------------------------
    // MESSAGING
    //------------------------------------------------------------------------
    @Override
    public boolean getRequest(ByteStorage request) {
        mLineReadTime = System.currentTimeMillis();
        String line;
        try {
            //Read lines, while line is not null
            while ((line = mBufferedReader.readLine()) != null
                    //AND (does not start with a common qualifier OR is not empty)
                    && (line.startsWith(COMMENT_QUALIFIER) || line.trim().isEmpty())) {
            }
            //If was end of file
            if (line == null) {
                mBufferedReader.close();
                return false;
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error while reading script file " + ioe.toString());
        }
        request.add(EncodingUtil.convertToByteArray(line));
        mLineReadTime = System.currentTimeMillis() - mLineReadTime;
        return true;
    }


    @Override
    public void writeResponse(byte[] response) {
        if (!Checker.isEmpty(response)) {
            try {
                System.out.write(response);
                System.out.write(MyProperties.OS_LINE_SEPARATOR.getBytes(MyProperties.ENCODING));
            } catch (IOException e) {
                throw new RuntimeError("Error while writing to CLI.");
            }
        }
    }


    @Override
    public long requestReadTime() {
        return mLineReadTime;
    }


    @Override
    public String senderInfo() {
        return mFilename;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("File", mFilename)
                .add("CommentQualifier", COMMENT_QUALIFIER)
                .build();
    }


}
