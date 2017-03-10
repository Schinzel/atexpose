package com.atexpose.dispatcher.logging.writer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import com.atexpose.MyProperties;
import com.atexpose.dispatcher.logging.LoggerType;
import io.schinzel.basicutils.Thrower;

/**
 * The purpose of this class is to write log data to file.
 *
 * @author schinzel
 */
public class LogFileWriter implements ILogWriter {
    /**
     * The file the data will be written to.
     */
    private RandomAccessFile mLogFile = null;
    /**
     * The name of the file log data is being written to.
     */
    private String mFilename;


    @Override
    public void setUp(String dispatcherName, LoggerType loggerType) {
        //Set up log directory
        checkDir(MyProperties.LOG_DIRECTORY);
        mFilename = MyProperties.LOG_DIRECTORY + MyProperties.FILE_SEPARATOR + dispatcherName + "_" + loggerType.name() + ".txt";
        try {
            this.createFileIfFileDoesNotExist(mFilename);
            mLogFile = new RandomAccessFile(mFilename, "rw");
            mLogFile.seek(mLogFile.length());
        } catch (IOException fnf) {
            throw new RuntimeException("Cannot create file '" + mFilename + "', make sure file name is valid and accessible.");
        }
    }


    @Override
    public void log(String logEntry) {
        try {
            this.createFileIfFileDoesNotExist(mFilename);
            mLogFile.write(logEntry.getBytes(Charset.forName(MyProperties.ENCODING)));
        } catch (IOException ex) {
            throw new RuntimeException("Error while writing log data to file");
        }
    }


    /**
     * The method name should say it all.
     *
     * @param filename  The name of the file.
     */
    private void createFileIfFileDoesNotExist(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
            mLogFile = new RandomAccessFile(mFilename, "rw");
        }
    }


    /**
     * Util method for checking a directory
     * <p>
     * The method throws an error if
     * 1) it has not read access
     * 2) the argument is not a directory
     * 3) the data dir could not be created
     *
     * @param directoryAsString    The name and possibly path to the directory
     */
    private static void checkDir(String directoryAsString) {
        File directory = new File(directoryAsString);
        //If dir does not exist AND attempt to create dir not was successful, throw error
        Thrower.throwIfTrue(!directory.exists() && !directory.mkdirs(), "Unable to create directory: '" + directory + "'");
        //If the path is not a directory, throw error
        Thrower.throwIfTrue(!directory.isDirectory(), "Path is not a directory: '" + directory + "'");
        //If this thread does not have read permission, throw error
        Thrower.throwIfTrue(!directory.canRead(), "No read access to data directory: '" + directory + "'");
    }

}
