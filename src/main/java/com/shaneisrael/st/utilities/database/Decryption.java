package com.shaneisrael.st.utilities.database;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * 
 * @author Shane
 *
 */
public class Decryption
{
    private static String decryptF(byte[] encryptionBytes, Key pkey, Cipher c) throws InvalidKeyException,

    BadPaddingException, IllegalBlockSizeException
{
    c.init(Cipher.DECRYPT_MODE, pkey);
    byte[] decrypt = c.doFinal(encryptionBytes);
    String decrypted = new String(decrypt);
    return decrypted;
}
}
