package com.shaneisrael.st.utilities;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.LinkDataSaver;
import com.shaneisrael.st.data.Preferences;

public class Upload extends Thread
{
    private String IMGUR_POST_URI = "http://api.imgur.com/2/upload.xml";
    private String IMGUR_API_KEY = "e9095ffb6a818372fdf2fa927cb46f27";
    private String CLIENT_ID = "492ba258a08820f"; // currently unused (for api
                                                  // version 3)
    private String CLIENT_SECRET = "8778dec43297a55f3e4768a5b8a5e6203ef12c4a"; // currently
                                                                               // unused
                                                                               // (for
                                                                               // api
                                                                               // version
                                                                               // 3)

    private String os = System.getProperty("os.name");

    private ByteArrayOutputStream baos;

    private String imgUrl[];
    private String delUrl[];

    private Thread uploadThread;

    private BufferedImage image;
    private Save save = new Save();

    private boolean reddit = false;

    public Upload()
    {

    }

    public Upload(BufferedImage img, boolean reddit)
    {
        this.image = img;
        this.reddit = reddit;
        uploadThread = new Thread(this);
        uploadThread.start();

    }

    @Override
    public void run()
    {
        // set working image
        if (os.indexOf("Mac") >= 0)
        {
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/uploadMac.png")).getImage());
        } else
        { // Windows
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/upload.gif")).getImage());
        }
        Main.displayInfoMessage("Uploading...", "Link will be available shortly");

        boolean uploaded = false;
        if (Preferences.DEFAULT_UPLOAD_PROVIDER == 0) // imgur
            uploaded = uploadToImgur(image);

        if (uploaded)
        {
            if (Preferences.AUTO_SAVE_UPLOADS)
            {
                save.saveUpload(image);
                new LinkDataSaver(imgUrl[0], delUrl[0], "upload(" + Preferences.TOTAL_SAVED_UPLOADS + ")");
            }

            if (!reddit)
            {
                ClipboardUtilities.setClipboard(imgUrl[0]);
                Main.displayInfoMessage("Upload Successful!", "Link has been copied to your clipboard");
            } else
            {
                OpenBrowser.openToReddit(imgUrl[0]);
                Main.displayInfoMessage("Upload Successful!", "Submitting link to Reddit");
            }

            SoundNotifications.playDing();

        } else
            Main.displayErrorMessage("Upload Failed!", "An unexpected error has occurred");

        // set back to the default image
        if (os.indexOf("Mac") >= 0)
        {
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/trayIconMac.png")).getImage());
        } else
        { // Windows
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/trayIcon.png")).getImage());
        }

        System.gc(); //garbage collect any unused memory by the upload thread and editor.
        this.interrupt();
    }

    private Boolean uploadToImgur(BufferedImage img)
    {
        /**
         * Uploads any buffered image to imgur and retrieves the link
         */
        try
        {
            // Creates Byte Array from picture
            baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            URL url = new URL(IMGUR_POST_URI);

            // encodes picture with Base64 and inserts api key
            String data = URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(Base64.encodeBase64String(baos.toByteArray()).toString(), "UTF-8");
            data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(IMGUR_API_KEY, "UTF-8");

            // opens connection and sends data
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;

            while ((decodedString = in.readLine()) != null)
            {
                imgUrl = decodedString.split("<large_thumbnail>");
                delUrl = decodedString.split("<delete_page>");
            }

            imgUrl = imgUrl[1].split("l.jpg</large_thumbnail>");
            imgUrl[0] += ".png";
            delUrl = delUrl[1].split("</delete_page>");

            in.close();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public String uploadImageFile(File file, String type)
    {
        try
        {
            // Creates Byte Array from picture
            baos = new ByteArrayOutputStream();
            Image image = ImageIO.read(file);
            ImageIO.write((RenderedImage) image, type, baos);
            image = null;
            URL url = new URL(IMGUR_POST_URI);

            // encodes picture with Base64 and inserts api key
            String data = URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(Base64.encodeBase64String(baos.toByteArray()).toString(), "UTF-8");
            data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(IMGUR_API_KEY, "UTF-8");

            // opens connection and sends data
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String decodedString;
            String response = null;
            while ((decodedString = in.readLine()) != null)
            {
                response = decodedString;
            }
            in.close();
            baos.close();
            return response;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "Error uploading file type: ." + type + " | Only image files allowed.";
    }

    private Boolean uploadToMinus(BufferedImage img)
    {
        return false;
    }

}
