package com.atexpose.util;

import io.schinzel.basicutils.UTF8;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Schinzel
 */
public class FileRW {

    /**
     * @param fileName The name of the file to check
     * @return True if the argument file exits, else false.
     */
    public static boolean fileExists(String fileName) {
        return FileRW.getInputStream(fileName) != null;
    }


    /**
     * Reads the argument file. Convert the file to the encoding indicated byte
     * the BOM in the file. If no BOM is present UTF-8 is used.
     *
     * @param fileName The file to read
     * @return The argument file content.
     */
    public static String readFileAsString(String fileName) {
        byte[] fileAsByteArray = FileRW.readFileAsByteArray(fileName);
        //Convert the file to a string
        return UTF8.getString(fileAsByteArray);
    }


    /**
     * @param fileName The file to return an input stream from
     * @return An input stream from a file on the file system or from a JAR.
     * Returns null if no such file or it cannot be read,
     */
    public static InputStream getInputStream(String fileName) {
        InputStream is;
        if (fileName.charAt(0) != '/') {
            fileName = "/" + fileName;
        }
        is = FileRW.class.getResourceAsStream(fileName);
        return is;
    }


    /**
     * Reads the argument file. Convert the file to the encoding indicated byte
     * the BOM in the file. If no BOM is present UTF-8 is used.
     *
     * @param fileName The file to read
     * @return The argument file content.
     */
    public static byte[] readFileAsByteArray(String fileName) {
        //Intermediate storage of file read into RAM
        ByteStorage byteStorage = new ByteStorage();
        try {
            InputStream is = FileRW.getInputStream(fileName);
            if (is == null) {
                throw new RuntimeException("No such file '" + fileName + "'");
            }
            //File will be read into this array
            byte[] bytes_from_file = new byte[512];
            //Indicates the number of bytes read
            int no_of_bytes_read;
            while ((no_of_bytes_read = is.read(bytes_from_file)) != -1) {
                byteStorage.add(bytes_from_file, 0, no_of_bytes_read);
            }
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file '" + fileName + "'. Java error message: " + e.getMessage());
        }
        return byteStorage.getBytes();
    }


}
