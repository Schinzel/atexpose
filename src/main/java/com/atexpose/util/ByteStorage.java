package com.atexpose.util;

import com.google.common.base.Charsets;
import io.schinzel.basicutils.UTF8;

/**
 * The purpose of this class is to hold an arbitrary amount of bytes.
 * The bytes are stored in a byte array for memory efficiency and for having
 * the data as native as possible to avoid inadvertent automatic
 * transformations as can happen with strings.
 *
 * @author Schinzel
 */
public class ByteStorage {
    /** The default max storage capacity. */
    private static final int DEFAULT_SIZE = 512;
    /** The default increment of storage capacity. */
    private static final int DEFAULT_INCREMENT = 512;
    /** Holds the data stored. */
    private byte[] mStorage = new byte[DEFAULT_SIZE];
    /** Index of the last byte in the storage array. */
    private int mUboundStorage = -1;
    //------------------------------------------------------------------------
    // MISC
    //------------------------------------------------------------------------


    /**
     * Clears the storage.
     */
    public void clear() {
        mUboundStorage = -1;
    }


    /**
     * @return The number of bytes currently stored.
     */
    public int getNoOfBytesStored() {
        return mUboundStorage + 1;
    }
    //------------------------------------------------------------------------
    // ADD
    //------------------------------------------------------------------------


    /**
     * Adds a byte to the storage.
     *
     * @param b Byte to add to the storage.
     */
    public void add(byte b) {
        mUboundStorage++;
        if (mUboundStorage == mStorage.length) {
            this.increaseSize();
        }
        mStorage[mUboundStorage] = b;
    }


    /**
     * @param ab A byte array to add to this storage.
     */
    public void add(byte[] ab) {
        this.add(ab, 0, ab.length);
    }


    /**
     * @param s A string to add to this storage as UTF8 bytes.
     */
    public void add(String s) {
        this.add(UTF8.getBytes(s));
    }


    /**
     * Appends a number of bytes to this object.
     *
     * @param ab     The source of the bytes.
     * @param start  From where to start retrieving the bytes.
     * @param length The number of bytes to retrieve from the argument array.
     */
    public void add(byte[] ab, int start, int length) {
        //If the new batch of bytes does not fit
        if (!this.doesNewBytesFit(length)) {
            //Increase size
            this.increaseSize(length);
        }
        if (length > 0) {
            //Copy the byte from argument array to storage array
            System.arraycopy(ab, start, mStorage, mUboundStorage + 1, length);
            //Set the new position of the storage array
            mUboundStorage += length;
        }
    }
    //------------------------------------------------------------------------
    // GET
    //------------------------------------------------------------------------


    /**
     * Converts the argument array to a string using the encoding in the BOM.
     * If no BOM is present, then the argument default encoding is used.
     *
     * @return The argument byte array converted to String.
     */
    public String getAsString() {
        return new String(mStorage, 0, mUboundStorage + 1, Charsets.UTF_8);
    }


    /**
     * @return Returns the all bytes stored in the array.
     */
    public byte[] getBytes() {
        return this.getBytes(0);
    }
    //------------------------------------------------------------------------
    // PRIVATE
    //------------------------------------------------------------------------


    /**
     * @param noOfBytesToDropFromTheEnd The number of bytes at the end not to
     *                                  return. For example: If there is a 100
     *                                  bytes stored in this object and this argument is ten, only
     *                                  the 90 first
     *                                  bytes will be returned.
     * @return The bytes stored in this object.
     */
    private byte[] getBytes(int noOfBytesToDropFromTheEnd) {
        int noOfBytesToReturn = mUboundStorage + 1 - noOfBytesToDropFromTheEnd;
        if (noOfBytesToReturn < 0) {
            noOfBytesToReturn = 0;
        }
        // Create a return array with the number of bytes of the pos in the
        // storage array
        byte[] abReturn = new byte[noOfBytesToReturn];
        // Copy the byte from storage array to return array
        System.arraycopy(mStorage, 0, abReturn, 0, abReturn.length);
        // Return the return array
        return abReturn;
    }


    /**
     * @return Returns true if the argument number of byte will fit into the
     * current array, else false.
     */
    private boolean doesNewBytesFit(int noOfNewBytes) {
        // Return if the number of argument bytes fits in the storage array
        return ((noOfNewBytes + mUboundStorage + 1) < mStorage.length);
    }


    /**
     * Increase the size of the internal storage.
     */
    private void increaseSize() {
        // Get the new size
        int newSize = mUboundStorage + 1 + DEFAULT_INCREMENT;
        // Create a new array
        byte[] newArray = new byte[newSize];
        // Copy the contents of the storage array to the new array
        System.arraycopy(mStorage, 0, newArray, 0, mStorage.length);
        // Set the storage array to be the new array
        mStorage = newArray;
    }


    /**
     * Increases the size of the internal storage array.
     *
     * @param noOfNewBytes The number of bytes that needs to fit in the array.
     */
    private void increaseSize(final int noOfNewBytes) {
        // Get the new size
        int newSize = this.getNewSize(noOfNewBytes);
        // Create a new array
        byte[] abNew = new byte[newSize];
        // Copy the contents of the storage array to the new array
        System.arraycopy(mStorage, 0, abNew, 0, mStorage.length);
        // Set the storage array to be the new array
        mStorage = abNew;
    }


    /**
     * @param noOfNewBytes number of bytes that need to fit in the array.
     * @return Returns the new size.
     */
    private int getNewSize(int noOfNewBytes) {
        // Calc the new size
        int newSize = mStorage.length + DEFAULT_INCREMENT;
        // If the new size is not large enough to contain the new no of bytes
        if (newSize < (mStorage.length + noOfNewBytes)) {
            // Set the new size the size of the storage array plus the new
            // number of bytes plus the increment
            newSize = mStorage.length + noOfNewBytes + DEFAULT_INCREMENT;
        }
        // Return the new size
        return newSize;
    }

}
