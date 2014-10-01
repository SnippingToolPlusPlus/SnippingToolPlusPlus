package com.shaneisrael.st.upload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/*
 * On hold until I feel like setting up a VM or unless somebody
 * else wants to mess with it until then. 
 * 
 * Preferences should probably have a drop down for hosts which contains
 * | Imgur |
 * |  FTP  |
 * and if FTP is selected, it should use the host, user, pass, and upload directory
 * that you supply in the preferences. 
 */
public class SimpleFTPUploader implements Runnable
{
    private static final int BUFFER_SIZE = 4096;
    private Thread self;
    
    private String host;
    private String user;
    private String pass;
    private String filePath;
    private String uploadPath;
    
    public SimpleFTPUploader(String host, String user,
        String pass, String uploadPath, String filePath)
    {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.filePath = filePath;
        this.uploadPath = uploadPath;
        
        self = new Thread(this);
        self.start();
    }
    @Override
    public void run()
    {
        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
        System.out.println("Upload URL: " + ftpUrl);

        try
        {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(filePath);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("File uploaded");
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
