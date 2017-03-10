package com.atexpose.dispatcher.logging.crypto;

/**
 * The purpose of this interface is to encrypt and decrypt strings. 
 *
 * @author schinzel
 */
public interface ICrypto {


    /**
     *
     * @param encryptedString
     * @return The argument string decrypted.
     */
    String decrypt(String encryptedString);


    /**
     *
     * @param clearTextString
     * @return The argument string encrypted as a hex string.
     */
    String encrypt(String clearTextString);
    
}
