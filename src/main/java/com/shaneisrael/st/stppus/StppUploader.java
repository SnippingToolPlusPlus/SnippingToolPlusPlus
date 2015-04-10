package com.shaneisrael.st.stppus;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.gson.Gson;
import com.shaneisrael.st.SnippingToolPlusPlus;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.notification.NotificationManager;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.utilities.AnimatedTrayIcon;
import com.shaneisrael.st.utilities.ClipboardUtilities;
import com.shaneisrael.st.utilities.SoundNotifications;
import com.shaneisrael.st.utilities.database.DBStats;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class StppUploader implements Runnable
{
    private static final AnimatedTrayIcon animatedIcon = AnimatedTrayIcon.getDefaultIcon();
    private static final Gson gson = new Gson();
    private BufferedImage image;
    private final String URI = "http://stppl.us/api/1/upload/?";
    private final String KEY = "f5d630e655b3806f24d81d0fe4715590";

    private Thread self;

    public StppUploader(BufferedImage image)
    {
        this.image = image;
    }

    public void upload()
    {
        doBeforeUpload();
        uploadAsync();
    }

    private void uploadAsync()
    {
        self = new Thread(this);
        self.start();
    }

    private void doBeforeUpload()
    {
        if (OperatingSystem.isWindows())
        {
            new Thread(animatedIcon, "upload-animation").start();
        } else
        {
            SnippingToolPlusPlus.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/uploadMac.png"))
                .getImage());
        }
        NotificationManager.getInstance().showNotification("uploading", STNotificationType.INFO);
    }

    private void doAfterUpload()
    {
        SoundNotifications.playDing();
        if (OperatingSystem.isWindows())
        {
            if (StppUploader.animatedIcon != null)
            {
                StppUploader.animatedIcon.stopAnimating();
            }
        } else
        {
            SnippingToolPlusPlus.trayIcon
                .setImage(new ImageIcon(this.getClass().getResource("/images/trayIconMac.png")).getImage());
        }

        image = null;
    }

    @Override
    public void run()
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            String base64bytes = Base64.encode(bytes);
            String data = "key=" + KEY;
            data += "&source=" + URLEncoder.encode(base64bytes, "UTF-8");//Base64.encodeBase64String(baos.toByteArray()).toString();
            data += "&format=txt";
            URL url = new URL(URI);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString = reader.readLine();
            reader.close();
            
            ClipboardUtilities.setClipboard(decodedString);
            DBStats.addHistory(decodedString, "");
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        doAfterUpload();
    }
}
