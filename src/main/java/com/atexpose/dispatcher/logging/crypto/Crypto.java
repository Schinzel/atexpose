package com.atexpose.dispatcher.logging.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import io.schinzel.basicutils.Checker;

/**
 * The purpose of this class is to encrypt and decrypt strings using AES.
 * Uses 128 bit encryption.
 *
 * @author Schinzel
 */
public class Crypto implements ICrypto {
    /**
     * Crypto options.
     */
    private static final String CRYPTO_OPTIONS = "AES/CTR/NoPadding";
    /**
     * The initialization vector. Commonly this is randomized. But we want
     * the same input to yield the same output, so this string is static.
     */
    private static final String INIT_VECTOR = "S5rrqZ4N43NOaRJl";
    /**
     * A hash map that hold instances of this crypto. The keys are crypto keys.
     * The values are crypto instance. Not creating the instance each time
     * makes the execution more than five times faster. Should also put less
     * strain on garbage collector.
     *
     * It is ok that this cache is static. If there are multiple instance of @Expose running and they have or
     * happen to have cryptos with the same key, they can share the same crypto instances.
     */
    private static final Map<String, Crypto> SINGLETON_MAP = new HashMap<>();
    /**
     * Holds an encrypter that encrypts values.
     */
    private final Cipher mEncrypter;
    /**
     * Holds a decrypted that decrypt values.
     */
    private final Cipher mDecrypter;


    private Crypto(String key) {
        if (Checker.isEmpty(key)) {
            throw new RuntimeException("Crypto key cannot be empty, mate.");
        }
        if (key.length() != 16) {
            throw new RuntimeException("Crypto key needs to be 16 bytes long.");
        }
        mEncrypter = this.getCipher(Cipher.ENCRYPT_MODE, key);
        mDecrypter = this.getCipher(Cipher.DECRYPT_MODE, key);
    }


    /**
     * Create or get and crypto instance. If the same key was used before, an
     * already created instance is returned.
     *
     * @param key The encryption key to use to encrypt and decrypt values.
     * @return An crypto instance.
     */
    public static Crypto getInstance(String key) {
        //Find an already constructed crypto instance with the argument key. 
        Crypto crypto = SINGLETON_MAP.get(key);
        //If there was no such instance
        if (crypto == null) {
            //Create it
            crypto = new Crypto(key);
            //... and store it in the map
            SINGLETON_MAP.put(key, crypto);
        }
        //Return the crypto instance
        return crypto;
    }


    /**
     * @param clearTextString
     * @return The argument string encrypted as a hex string.
     */
    @Override
    public String encrypt(String clearTextString) {
        try {
            byte[] encrypted = mEncrypter.doFinal(clearTextString.getBytes(Charset.forName("UTF-8")));
            return Hex.encodeHexString(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new RuntimeException("Error when encrypting a string. " + ex.getMessage());
        }
    }


    /**
     * @param encryptedString An encrypted string.
     * @return The argument string decrypted.
     */
    @Override
    public String decrypt(String encryptedString) {
        try {
            byte[] byteArrToDecrypt = Hex.decodeHex(encryptedString.toCharArray());
            byte[] decryptedByteArray = mDecrypter.doFinal(byteArrToDecrypt);
            return new String(decryptedByteArray, Charset.forName("UTF-8"));
        } catch (IllegalBlockSizeException | BadPaddingException | DecoderException ex) {
            throw new RuntimeException("Error when decrypting a string." + ex.getMessage());
        }
    }


    /**
     * Used by the constructor to set up.
     *
     * @param mode Allows values are Cipher.ENCRYPT_MODE and Cipher.DECRYPT_MODE
     * @param key  En crypto key to use.
     * @return A cipher
     */
    private Cipher getCipher(int mode, String key) {
        try {
            IvParameterSpec initVector = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance(CRYPTO_OPTIONS);
            cipher.init(mode, skeySpec, initVector);
            return cipher;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            throw new RuntimeException("Error when encrypting or decrypting a string. " + ex.getMessage());
        }
    }

}
