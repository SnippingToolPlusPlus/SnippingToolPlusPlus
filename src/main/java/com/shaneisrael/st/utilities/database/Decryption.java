package com.shaneisrael.st.utilities.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.shaneisrael.st.Config;

/**
 * 
 * @author Shane
 *
 */
public class Decryption
{
    public static void getUserDetails() throws Exception
    {
        URL keyIn = new URL(Config.WEBSITE_URL+"/key");
        
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        InputStream is = null;
        try {
          is = keyIn.openStream ();
          byte[] byteChunk = new byte[4096];
          int n;

          while ( (n = is.read(byteChunk)) > 0 ) {
            bais.write(byteChunk, 0, n);
          }
        }
        catch (IOException e) {
          e.printStackTrace ();
        }
        finally {
          if (is != null) { is.close(); }
        }
        
        byte[] encryptionBytes = Files.readAllBytes(Paths.get(Decryption.class.getResource("/data").toURI()));
        byte[] encryptedKey = bais.toByteArray();
        bais.close();
        
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
