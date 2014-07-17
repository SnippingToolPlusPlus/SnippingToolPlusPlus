package com.shaneisrael.st.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.shaneisrael.st.Config;
import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.LinkDataSaver;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.upload.ImgurUploader;

public class Upload extends Thread
{
    private Thread uploadThread;

    private BufferedImage image;
    private Save save = new Save();

    private boolean reddit = false;

    private static AnimatedTrayIcon animatedIcon = new AnimatedTrayIcon("/images/upload/", 14, 38);

    private String imageUrl, deleteUrl;

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
        if (OperatingSystem.isWindows())
        {
            new Thread(animatedIcon, "upload-animation").start();
        } else
        {
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/uploadMac.png")).getImage());
        }
        Main.displayInfoMessage("Uploading...", "Link will be available shortly");

        boolean uploaded = uploadToImgur(image);

        if (uploaded)
        {
            if (Preferences.getInstance().isAutoSaveEnabled())
            {
                save.saveUpload(image);
                new LinkDataSaver(imageUrl, deleteUrl, "upload(" + Preferences.TOTAL_SAVED_UPLOADS + ")");
            }

            if (!reddit)
            {
                ClipboardUtilities.setClipboard(imageUrl);
                Main.displayInfoMessage("Upload Successful!", "Link has been copied to your clipboard");
            } else
            {
                Browser.openToReddit(imageUrl);
                Main.displayInfoMessage("Upload Successful!", "Submitting link to Reddit");
            }

            SoundNotifications.playDing();

        } else
        {
            Main.displayErrorMessage("Upload Failed!", "An unexpected error has occurred");
        }

        if (OperatingSystem.isWindows())
        {
            if (Upload.animatedIcon != null)
            {
                Upload.animatedIcon.stopAnimating();
            }
        } else
        {
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/trayIconMac.png")).getImage());
        }

        System.gc(); //garbage collect any unused memory by the upload thread and editor.
        this.interrupt();
    }

    private boolean uploadToImgur(BufferedImage img)
    {
        ImgurUploader uploader = new ImgurUploader(Config.USER_AGENT);
        boolean success = false;
        try
        {
            imageUrl = uploader.upload(img);
            success = true;
        } catch (IOException e)
        {
            System.out.println("Failed to upload image to server");
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public String uploadImageFile(File file, String type)
    {
        ImgurUploader uploader;
        uploader = new ImgurUploader(Config.USER_AGENT);
        String urlOrMessage;
        try
        {
            urlOrMessage = uploader.upload(file);
        } catch (IOException e)
        {
            System.out.println("Failed to upload image to server");
            urlOrMessage = "Failed to upload file: " + e.getMessage();
        }
        return urlOrMessage;
    }
}
