package com.atexpose.dispatcher.logging;

import io.schinzel.basicutils.crypto.cipher.ICipher;

/**
 *
 * @author Schinzel
 */
public class TestCipher implements ICipher {
    static final String ENC_PREFIX = "ENCRYPTED:";

    @Override
    public String decrypt(String encryptedString) {
        throw new RuntimeException("This should not be used when testing loggers");
    }


    @Override
    public String encrypt(String clearTextString) {
         return ENC_PREFIX + clearTextString;     
    }
    
    
}
