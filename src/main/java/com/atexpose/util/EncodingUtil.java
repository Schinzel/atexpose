package com.atexpose.util;

import com.atexpose.errors.RuntimeError;
import com.google.common.base.Charsets;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Utility class for handling encoding and decoding between strings and byte
 * arrays.
 *
 * @author Schinzel
 */
public class EncodingUtil {
    //The two static arrays below are synced.
    //The names of the unicode transformation formats. Although UTF stands for USC Transformation Format.
    static final String[] ARR_ENCODING_NAMES = {"UTF-8", "UTF-16BE", "UTF-16LE"};
    //The BOMs look like for the different encodings.
    private static final byte[][] ARR_ENCODING_BOM = {
            {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF},
            {(byte) 0xFE, (byte) 0xFF},
            {(byte) 0xFF, (byte) 0xFE},};
    //Index of the default encoding
    private static final int DEFAULT_ENCODING_INDEX = 0;


    /**
     * Constructor is private as all methods are static. I.e. no need to create
     * the class.
     */
    private EncodingUtil() {
    }


    /**
     * @param str String to convert.
     * @return The argument string as an byte array.
     */
    public static byte[] convertToByteArray(String str) {
        return EncodingUtil.convertToByteArray(str, getDefaultEncoding(), false);
    }


    /**
     * Converts the argument string to the argument encoding. Throws an error if
     * the encoding is not supported by the JRE.
     *
     * @param str             The string to transform
     * @param sEncoding       The encoding of returned the byte array
     * @param booAddBomPrefix If true, a BOM is added to the return
     * @return The argument string as a byte array.
     */
    static byte[] convertToByteArray(String str, String sEncoding, boolean booAddBomPrefix) {
        byte[] ab;
        byte[] abBOM = null;
        if (Checker.isEmpty(str)) {
            return EmptyObjects.EMPTY_BYTE_ARRAY;
        }
        //If encoding-string argument is empty
        if (Checker.isEmpty(sEncoding)) {
            ab = str.getBytes(Charsets.UTF_8);
        }//else, i.e. there was an argument encoding string
        else {
            //If is to add aBOM prefix
            if (booAddBomPrefix) {
                //Get BOM
                abBOM = getBOM(sEncoding);
            }
            ab = str.getBytes(Charset.forName(sEncoding));
        }
        //Add BOM and return
        return ArrayUtil.concat(abBOM, ab);
    }
    //------------------------------------------------------------------------
    // TRANSFORM TO STRING
    //------------------------------------------------------------------------


    /**
     * Converts the argument array to a string using the encoding in the BOM. If
     * no BOM is present, then the argument default encoding is used.
     *
     * @param byteArray The bytes to convert to string
     * @return The argument byte array converted to String.
     */
    public static String convertToString(byte[] byteArray) {
        return convertToString(byteArray, getDefaultEncoding(), byteArray.length - 1);
    }


    /**
     * Converts the argument array to a string using the encoding in the BOM. If
     * no BOM is present, then the argument default encoding is used.
     *
     * @param byteArray       The bytes to convert to string
     * @param defaultEncoding The encoding to use if no BOM is present in
     *                        byteArray
     * @param indexOfLastByte The index of the last byte to convert.
     * @return The argument byte array converted to String.
     */
    static String convertToString(byte[] byteArray, String defaultEncoding, int indexOfLastByte) {
        //The length of the byte order mark.
        int startPosBOM;
        //Get the encoding of the byte array
        int encodingIndex = getEncodingIndex(byteArray);
        String encoding;
        //If no byte order mark was found, which means no encoding was found
        if (encodingIndex == -1) {
            //Use default encoding
            encoding = defaultEncoding;
            startPosBOM = 0;
        }//else, i.e. a BOM and thereby an encoding was found
        else {
            //Get the name of the encoding
            encoding = ARR_ENCODING_NAMES[encodingIndex];
            //Get the length of the encoding.
            startPosBOM = ARR_ENCODING_BOM[encodingIndex].length;
        }
        try {
            return new String(byteArray, startPosBOM, (indexOfLastByte - startPosBOM + 1), encoding);
        } catch (final UnsupportedEncodingException uee) {
            throw new RuntimeError("Encoding '" + encoding + "' not supported.");
        }
    }


    /**
     * Get the byte array BOM of the argument encoding.
     *
     * @param encodingName The name of the encoding
     * @return The bytes of the argument BOM. If no such BOM, null is returned.
     */
    static byte[] getBOM(String encodingName) {
        int encodingIndex = getEncodingIndex(encodingName);
        if (encodingIndex != -1) {
            return ARR_ENCODING_BOM[encodingIndex];
        } else {
            return EmptyObjects.EMPTY_BYTE_ARRAY;
        }
    }


    /**
     * @return The name of the default encoding.
     */
    private static String getDefaultEncoding() {
        return ARR_ENCODING_NAMES[DEFAULT_ENCODING_INDEX];
    }


    /**
     * Gets the internal index of the encoding. The index points to the arrays
     * above.
     *
     * @param ab The arbitrary byte array from which the BOM is to derived.
     * @return The index of the encoding. -1 if no recognizable BOM was found.
     */
    private static int getEncodingIndex(byte[] ab) {
        if (ab == null || ab.length == 0) {
            return -1;
        }
        //Go through all BOMs
        for (int currentBOM = 0; currentBOM < ARR_ENCODING_BOM.length; currentBOM++) {
            //Go through all byte in current BOM
            for (int i = 0; ((i < ARR_ENCODING_BOM[currentBOM].length)
                    && (i < ab.length) && (ab[i] == ARR_ENCODING_BOM[currentBOM][i])); i++) {
                //If is last and last byte is equal
                if (((i + 1) == ARR_ENCODING_BOM[currentBOM].length)
                        && (ab[i] == ARR_ENCODING_BOM[currentBOM][i])) {
                    //Then the current BOM is the BOM in the file, hence return current BOM index.
                    return currentBOM;
                }
            }
        }
        return -1;
    }


    /**
     * Returns the internal index of the encoding.
     *
     * @param encodingName The name of the encoding.
     * @return Index of the encoding if supported. Else -1.
     */
    private static int getEncodingIndex(String encodingName) {
        for (int i = 0; i < ARR_ENCODING_NAMES.length; i++) {
            if (ARR_ENCODING_NAMES[i].equalsIgnoreCase(encodingName)) {
                return i;
            }
        }
        return -1;
    }

}
