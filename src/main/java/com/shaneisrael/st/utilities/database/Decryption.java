package com.shaneisrael.st.utilities.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author Shane
 *
 */
public class Decryption
{
    public static void getUserDetails() throws Exception
    {
        byte[] encryptionBytes = Files.readAllBytes(Paths.get(Decryption.class.getResource("/data.enc").toURI()));
        byte[] encryptedKey = Files.readAllBytes(Paths.get(Decryption.class.getResource("/key.enc").toURI()));
        
        Key pkey = new SecretKeySpec(encryptedKey, 0, encryptedKey.length, "DESede");
        Cipher c = Cipher.getInstance("DESede");
        
        
        c.init(Cipher.DECRYPT_MODE, pkey);
        byte[] decrypt = c.doFinal(encryptionBytes);
        String decrypted = new String(decrypt);
        
        String[] split = decrypted.toString().split("\\|");
        DBConnection.setUser(split[0]);
        DBConnection.setPassword(split[1]);
    }
}
