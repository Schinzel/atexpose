package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.crypto.ICrypto;

/**
 *
 * @author Schinzel
 */
public class TestCrypto implements ICrypto {
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
