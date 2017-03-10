package com.atexpose.dispatcher.logging.crypto;

import com.atexpose.dispatcher.logging.crypto.Crypto;
import com.atexpose.dispatcher.wrapper.FunnyChars;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 *
 * @author Schinzel
 */
public class CryptoTest {
    private static final String STRING_TO_ENCRYPT = "This is a longish string that is we use to test the performance of the crypto";

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testConstructorKeyEmpty() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Crypto key cannot be empty");
        Crypto.getInstance("");
    }


    @Test
    public void testConstructorKeyWrongSize() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Crypto key needs to be 16 bytes long");
        Crypto.getInstance("12345");
    }


    @Test
    public void testObjectCache() {
        String key = "dlcmpz1i4shnwukf";
        Crypto cryptoInstance1 = Crypto.getInstance(key);
        Crypto cryptoInstance2 = Crypto.getInstance(key);
        assertEquals(cryptoInstance1, cryptoInstance2);
    }


    @Test
    public void testShortString() {
        String clearTextString = "a";
        Crypto crypto = Crypto.getInstance("dlcmpz1i4shnwukf");
        String encryptedString = crypto.encrypt(clearTextString);
        String decryptedString = crypto.decrypt(encryptedString);
        assertEquals(clearTextString, decryptedString);
    }


    @Test
    public void testLongString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3000; i++) {
            sb.append("a");
        }
        for (int i = 0; i < 3000; i++) {
            sb.append("b");
        }
        for (int i = 0; i < 3000; i++) {
            sb.append("c");
        }
        String clearTextString = sb.toString();
        Crypto crypto = Crypto.getInstance("dlcmpz1i4shnwukf");
        String encryptedString = crypto.encrypt(clearTextString);
        String decryptedString = crypto.decrypt(encryptedString);
        assertEquals(clearTextString, decryptedString);
    }


    @Test
    public void testFunnyCharsEncryption() {
        Crypto crypto = Crypto.getInstance("dlcmpz1i4shnwukf");
        String clearTextString, encryptedString, decryptedString;
        for (FunnyChars funnyChars : FunnyChars.values()) {
            clearTextString = funnyChars.getString();
            encryptedString = crypto.encrypt(clearTextString);
            decryptedString = crypto.decrypt(encryptedString);
            assertEquals(clearTextString, decryptedString);
        }
    }


    @Test
    public void testPerformanceEncrypt() {
        Crypto crypto = Crypto.getInstance("dlcmpz1i4shnwukf");
        crypto.encrypt(STRING_TO_ENCRYPT);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            crypto.encrypt(STRING_TO_ENCRYPT);
        }
        long executionTime = System.currentTimeMillis() - start;
        /**
         * It should take less than 200 ms to encrypt a string 10 000 times
         * On an 2011 max book pro (2,4 GHz Intel Core i7) it took aprox 75 ms.
         */
        assertTrue(executionTime < 200);
    }


    @Test
    public void testPerformanceDecrypt() {
        Crypto crypto = Crypto.getInstance("dlcmpz1i4shnwukf");
        String stringToDecrypt = crypto.encrypt(STRING_TO_ENCRYPT);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            crypto.decrypt(stringToDecrypt);
        }
        long executionTime = System.currentTimeMillis() - start;
        /**
         * It should take less than 300 ms to decrypt a string 10 000 times
         * On an 2011 max book pro (2,4 GHz Intel Core i7) it took aprox 80 ms.
         */
        assertTrue(executionTime < 300);
    }

}
