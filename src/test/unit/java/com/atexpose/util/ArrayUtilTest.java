package com.atexpose.util;

import io.schinzel.basicutils.EmptyObjects;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author schinzel
 */
public class ArrayUtilTest {


    @Test
    public void testConcatByteArrays() {
        byte[] b1, b2, result;
        b1 = null;
        b2 = null;
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, result);
        //
        b1 = EmptyObjects.EMPTY_BYTE_ARRAY;
        b2 = null;
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, result);
        //
        b1 = null;
        b2 = EmptyObjects.EMPTY_BYTE_ARRAY;
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, result);
        //
        b1 = EmptyObjects.EMPTY_BYTE_ARRAY;
        b2 = EmptyObjects.EMPTY_BYTE_ARRAY;
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, result);
        //
        b1 = new byte[]{1, 2, 3};
        b2 = null;
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(b1, result);
        //
        b1 = null;
        b2 = new byte[]{1, 2, 3};
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(b2, result);
        //
        b1 = new byte[]{1, 2, 3};
        b2 = new byte[]{4, 5, 6};
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, result);
        //
        b1 = new byte[]{1, 2, 3};
        b2 = new byte[]{4};
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(new byte[]{1, 2, 3, 4}, result);
        //
        b1 = new byte[]{1};
        b2 = new byte[]{4, 5, 6};
        result = ArrayUtil.concat(b1, b2);
        assertArrayEquals(new byte[]{1, 4, 5, 6}, result);
    }


}
