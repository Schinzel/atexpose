package com.atexpose.util;

import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Checker;

/**
 * This class holds a set of method for array operations.
 *
 * @author Schinzel
 */
public class ArrayUtil {


    /**
     * The Apache commons ArrayUtils.addAll(arr1, arr2) and Guava Bytes.concat(arr1, arr2)
     * have problems handling null argument arrays.
     *
     * @param arr1 An array
     * @param arr2 An other array
     * @return The argument arrays concatenated.
     */
    public static byte[] concat(byte[] arr1, byte[] arr2) {
        if (Checker.isEmpty(arr1) && !Checker.isEmpty(arr2)) {
            return arr2;
        }
        //If one array is empty return the other
        if (!Checker.isEmpty(arr1) && Checker.isEmpty(arr2)) {
            return arr1;
        }
        //If both arrays are empty return empty array
        if (Checker.isEmpty(arr1) && Checker.isEmpty(arr2)) {
            return EmptyObjects.EMPTY_BYTE_ARRAY;
        }
        //Return array
        final byte[] abReturn = new byte[arr1.length + arr2.length];
        //Copy first array
        System.arraycopy(arr1, 0, abReturn, 0, arr1.length);
        //Copy second array
        System.arraycopy(arr2, 0, abReturn, arr1.length, arr2.length);
        return abReturn;
    }

}
