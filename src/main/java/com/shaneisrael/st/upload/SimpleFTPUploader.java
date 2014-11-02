package com.shaneisrael.st.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.prefs.Preferences;
import com.sun.jmx.snmp.Timestamp;

/**TODO
 * 
 * @author Shane
 * 
 * Add a way to get the image files from the specified path
 * so that we can name images appropriately.
 *
 */
public class SimpleFTPUploader implements Runnable
{
    private static final int BUFFER_SIZE = 4096;
    private Thread self;
    private String host;
    private String user;
    private String pass;
    private File image;
    private String uploadPath;
    
    public SimpleFTPUploader(File imageFile)
    {
        try
        {
            this.host = Preferences.getInstance().getFTPHost();
            this.user = Preferences.getInstance().getFTPUser();
            this.pass = Preferences.getInstance().getFTPPassword();
            this.uploadPath = Preferences.getInstance().getFTPPath();
            this.image = imageFile;
            self = new Thread(this);
            self.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void run()
    {
        String filename = "";
        if(!Preferences.getInstance().getFTPGenerateTimestamp())
            filename = askForFileName();
        else
            filename = getTimeStamp();
        
        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath + filename + ".png");

        Main.showNotification("uploading-ftp", STNotificationType.INFO);
        try
        {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(image);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

        } catch (IOException ex)
        {
            Main.showNotification("uploading-ftp-failed", STNotificationType.ERROR);
            ex.printStackTrace();
        }
        Main.showNotification("upload-ftp-done", STNotificationType.SUCCESS);
    }
    private String askForFileName()
    {
        String filename = JOptionPane.showInputDialog(null, "Desired file name?", "Uploading to FTP...", JOptionPane.INFORMATION_MESSAGE);
        if(filename == null || filename.equals(""))
        {
            JOptionPane.showMessageDialog(null,"Please input a valid file name!", "Invalid File Name",JOptionPane.WARNING_MESSAGE);
            return askForFileName();
        }
        return filename;
    }
    private String getTimeStamp()
    {
        String timeStamp = new SimpleDateFormat("MM-dd-yy HH:mm:ss").format(new Date());
        return timeStamp;
    }
}
