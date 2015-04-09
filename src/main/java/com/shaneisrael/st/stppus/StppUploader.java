package com.shaneisrael.st.stppus;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;



import com.shaneisrael.st.SnippingToolPlusPlus;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.notification.NotificationManager;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.utilities.AnimatedTrayIcon;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class StppUploader implements Runnable
{
    private static final AnimatedTrayIcon animatedIcon = AnimatedTrayIcon.getDefaultIcon();
    private BufferedImage image;
    private final String URI = "http://stppl.us/api/1/upload/";
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
            // Creates Byte Array from picture
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, "jpg", baos);
            URL url = new URL(URI);
            
            byte[] bytes = baos.toByteArray();
            // encodes picture with Base64 and inserts api key
            String base64bytes = Base64.encode(bytes);
            String data = "?key=" + KEY;
            data += "&source=" + base64bytes;//Base64.encodeBase64String(baos.toByteArray()).toString();
            data += "==";

            // opens connection and sends data
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(data);
//            wr.flush();

            DataInputStream in = new DataInputStream(conn.getInputStream());
            String decodedString;

            while (null != ((decodedString = in.readLine())))
            {
                //temporary. Should print out a json response if working correctly
                System.out.println(decodedString);
            }

            in.close();
            //wr.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        doAfterUpload();
    }
}
