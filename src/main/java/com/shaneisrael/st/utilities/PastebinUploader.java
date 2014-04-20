package com.shaneisrael.st.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;

import com.shaneisrael.st.Main;

public class PastebinUploader extends Thread
{
    private String PASTEBIN_URI = "http://pastebin.com/api/api_post.php";
    private String PASTEBIN_LOGIN_URI = "http://pastebin.com/api/api_login.php";
    private String PASTEBIN_API_KEY = "4460c93cce03147d2142f26a02941642";
    private String PASTEBIN_USER_KEY;
    private String pastebinError = "Unknown Error";

    private Thread uploadThread;
    private String uploadText;

    private ByteArrayOutputStream baos;

    public PastebinUploader(String text)
    {
        uploadText = text;
        uploadThread = new Thread(this);
        uploadThread.start();

    }

    @Override
    public void run()
    {
        Main.displayInfoMessage("Uploading Clipboard Text...", "Your upload will be done shortly.");

        // set working image
        Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/upload.gif")).getImage());
        boolean uploaded = false;
        uploaded = uploadToPastebin(uploadText);
        if (uploaded)
        {
            Main.displayInfoMessage("Upload Successful!", "Link copied to clipboard.");
            SoundNotifications.playDing();
        } else
            Main.displayErrorMessage("Error Uploading!", "Error Code: " + pastebinError);

        // set back to the default image
        Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/trayIcon.png")).getImage());

    }

    private boolean uploadToPastebin(String uploadText2)
    {
        try
        {
            // Creates Byte Array from picture
            baos = new ByteArrayOutputStream();
            URL url = new URL(PASTEBIN_URI);
            // encodes text with Base64 and inserts api key
            String data = URLEncoder.encode("text", "UTF-8") + "="
                    + URLEncoder.encode(Base64.encodeBase64String(baos.toByteArray()).toString(), "UTF-8");
            data += "&" + URLEncoder.encode("api_dev_key", "UTF-8") + "="
                    + URLEncoder.encode(PASTEBIN_API_KEY, "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_format", "UTF-8") + "=" + URLEncoder.encode("Text", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_private", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_name", "UTF-8") + "="
                    + URLEncoder.encode("Snipping Tool++ Paste", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_expire_date", "UTF-8") + "=" + URLEncoder.encode("1M", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_code", "UTF-8") + "=" + URLEncoder.encode(uploadText2, "UTF-8");
            data += "&" + URLEncoder.encode("api_option", "UTF-8") + "=" + URLEncoder.encode("paste", "UTF-8");
            // opens connection and sends data
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;

            while ((decodedString = in.readLine()) != null)
                if (decodedString.contains("pastebin.com"))
                    ClipboardUtilities.setClipboard(decodedString);
            in.close();

            return true;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
