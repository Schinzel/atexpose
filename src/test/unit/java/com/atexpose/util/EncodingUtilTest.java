package com.atexpose.util;

import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.FunnyChars;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author schinzel
 */
public class EncodingUtilTest {


    @Test
    public void testConvertToByteArray_EmptyString(){
        byte[] ab =  EncodingUtil.convertToByteArray(null);
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, ab);
        ab = EncodingUtil.convertToByteArray("");
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, ab);
    }

    @Test
    public void testBackAndForthConversionWithBOM() {
        //Go through encodings
        for (String encodingName : EncodingUtil.ARR_ENCODING_NAMES) {
            //Go through funny chars
            for (FunnyChars funnyString : FunnyChars.values()) {
                //Get the current string
                String inputString = funnyString.getString();
                //Convert funny char string to byte array with BOM
                byte[] byteArrayWithBOM = EncodingUtil.convertToByteArray(inputString, encodingName, true);
                //Convert byte array to String
                String outputString = EncodingUtil.convertToString(byteArrayWithBOM);
                //Check that input string and output string are equal
                assertEquals(inputString, outputString);
            }
        }
    }
    
    
    @Test
    public void testBackAndForthConversionWithoutBOM() {
        //Go through encodings
        for (String encodingName : EncodingUtil.ARR_ENCODING_NAMES) {
            //Go through funny chars
            for (FunnyChars funnyString : FunnyChars.values()) {
                //Get the current string
                String inputString = funnyString.getString();
                //Convert funny char string to byte array with BOM
                byte[] byteArray = EncodingUtil.convertToByteArray(inputString, encodingName, false);
                byte[] bom = EncodingUtil.getBOM(encodingName);
                byte[] byteArrayWithBOM = ArrayUtil.concat(bom, byteArray);
                //Convert byte array to String
                String outputString = EncodingUtil.convertToString(byteArrayWithBOM);
                //Check that input string and output string are equal
                assertEquals(inputString, outputString);
            }
        }
    }

    @Test
    public void testBackAndForthConversionWithoutBOM2() {
        //Go through encodings
        for (String encodingName : EncodingUtil.ARR_ENCODING_NAMES) {
            //Go through funny chars
            for (FunnyChars funnyString : FunnyChars.values()) {
                //Get the current string
                String inputString = funnyString.getString();
                //Convert funny char string to byte array with BOM
                byte[] byteArray = EncodingUtil.convertToByteArray(inputString, encodingName, false);
                //Convert byte array to String
                String outputString = EncodingUtil.convertToString(byteArray, encodingName, byteArray.length-1);
                //Check that input string and output string are equal
                assertEquals(inputString, outputString);
            }
        }
    }
}
