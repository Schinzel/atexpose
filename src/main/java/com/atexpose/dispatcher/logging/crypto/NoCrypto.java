package com.atexpose.dispatcher.logging.crypto;

/**
 * The purpose of this class is offer a crypto implementation that does not
 * encrypt nor decrypt the strings. This so that an ICrypto object always can be
 * passed to methods or constructors and there has to be no checks for if
 * the crypto object has been set or not. 
 *
 * @author schinzel
 */
public class NoCrypto implements ICrypto {

    @Override
    public String decrypt(String encryptedString) {
        return encryptedString;
    }


    @Override
    public String encrypt(String clearTextString) {
        return clearTextString;
    }

}
