package com.atexpose.util;

import static org.junit.Assert.*;
import org.junit.Test;
import io.schinzel.basicutils.EmptyObjects;

/**
 *
 * @author Schinzel
 */
public class ByteStorageTest {

    @Test
    public void testSingleAdd() {
        ByteStorage bs = new ByteStorage();
        //Add a set of byte
        for (int i = 0; i < 27; i++) {
            bs.add((byte) 65);
        }
        assertEquals(27, bs.getNoOfBytesStored());
        //Clear
        bs.clear();
        assertEquals(0, bs.getNoOfBytesStored());
        //Add 10 "A":s
        for (int i = 0; i < 10; i++) {
            bs.add((byte) 65);
        }
        assertEquals(10, bs.getNoOfBytesStored());
        assertEquals("AAAAAAAAAA", bs.getAsString());
        assertArrayEquals(new byte[]{65, 65, 65, 65, 65, 65, 65, 65, 65, 65}, bs.getBytes());
        //Clear
        bs.clear();
        assertEquals(0, bs.getNoOfBytesStored());
        assertEquals("", bs.getAsString());
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, bs.getBytes());
        //Add a single byte
        bs.add((byte) 66);
        assertEquals("B", bs.getAsString());
        assertArrayEquals(new byte[]{66}, bs.getBytes());
        bs.clear();
        //Add bytes to trigger size increase
        for (int i = 0; i < 2000; i++) {
            bs.add((byte) 65);
        }
        assertEquals(2000, bs.getNoOfBytesStored());
        bs.clear();
        assertEquals(0, bs.getNoOfBytesStored());
    }


    /**
     * Testa att lägga till extremvärden -128 127
     * en lång byte array
     */
    @Test
    public void testExtremeBytes() {
        ByteStorage bs = new ByteStorage();
        byte[] ab = new byte[]{-128, 127};
        bs.add(ab);
        assertEquals(2, bs.getNoOfBytesStored());
        assertArrayEquals(ab, bs.getBytes());
    }


    @Test
    public void testAddByteArray() {
        ByteStorage bs = new ByteStorage();
        assertEquals(0, bs.getNoOfBytesStored());
        assertEquals("", bs.getAsString());
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, bs.getBytes());
        bs.add(new byte[]{65, 65, 65});
        bs.add(new byte[]{66, 66, 66});
        bs.add(new byte[]{67, 67, 67});
        assertEquals(9, bs.getNoOfBytesStored());
        assertEquals("AAABBBCCC", bs.getAsString());
        assertArrayEquals(new byte[]{65, 65, 65, 66, 66, 66, 67, 67, 67}, bs.getBytes());
        bs.clear();
        assertEquals(0, bs.getNoOfBytesStored());
        assertEquals("", bs.getAsString());
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, bs.getBytes());
    }


    @Test
    public void testAddLargeByteArray() {
        ByteStorage bs = new ByteStorage();
        int size = 1000;
        byte[] ab = new byte[size];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            ab[i] = (byte) 65;
            sb.append("A");
        }
        bs.add(ab);
        assertEquals(size, bs.getNoOfBytesStored());
        assertEquals(sb.toString(), bs.getAsString());
        assertArrayEquals(ab, bs.getBytes());
        bs.add(ab);
        bs.add(ab);
        int threeTimesSize = size * 3;
        assertEquals(threeTimesSize, bs.getNoOfBytesStored());
        bs.clear();
        assertEquals(0, bs.getNoOfBytesStored());
        assertEquals("", bs.getAsString());
        assertArrayEquals(EmptyObjects.EMPTY_BYTE_ARRAY, bs.getBytes());
    }


    @Test
    public void testAddPartialArray() {
        ByteStorage bs = new ByteStorage();
        byte[] ab = new byte[]{65, 66, 67};
        bs.add(ab, 0, 1);
        assertEquals(1, bs.getNoOfBytesStored());
        assertEquals("A", bs.getAsString());
        assertArrayEquals(new byte[]{65}, bs.getBytes());
        //
        bs.clear();
        bs.add(ab, 1, 1);
        assertEquals(1, bs.getNoOfBytesStored());
        assertEquals("B", bs.getAsString());
        assertArrayEquals(new byte[]{66}, bs.getBytes());
        //
        bs.clear();
        bs.add(ab, 2, 1);
        assertEquals(1, bs.getNoOfBytesStored());
        assertEquals("C", bs.getAsString());
        assertArrayEquals(new byte[]{67}, bs.getBytes());
        //
        bs.clear();
        bs.add(ab, 0, 2);
        assertEquals(2, bs.getNoOfBytesStored());
        assertEquals("AB", bs.getAsString());
        assertArrayEquals(new byte[]{65, 66}, bs.getBytes());
        //
        bs.clear();
        bs.add(ab, 1, 2);
        assertEquals(2, bs.getNoOfBytesStored());
        assertEquals("BC", bs.getAsString());
        assertArrayEquals(new byte[]{66, 67}, bs.getBytes());
        //
        bs.clear();
        bs.add(ab, 0, 3);
        assertEquals(3, bs.getNoOfBytesStored());
        assertEquals("ABC", bs.getAsString());
        assertArrayEquals(new byte[]{65, 66, 67}, bs.getBytes());
    }


    @Test
    public void testFunnyChars() {
        ByteStorage bs = new ByteStorage();
        byte[] ab = new byte[]{32, 33, 35, 123, 124, 94};
        String str = " !#{|^";
        bs.add(ab);
        assertEquals(ab.length, bs.getNoOfBytesStored());
        assertArrayEquals(ab, bs.getBytes());
        assertEquals(str, bs.getAsString());
    }

}
