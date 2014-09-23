package com.shaneisrael.st.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import us.snippingtoolpluspl.notifications.STNotificationType;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.LinkDataSaver;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.imgur.ImgurImage;
import com.shaneisrael.st.imgur.ImgurResponse;
import com.shaneisrael.st.imgur.ImgurResponseListener;
import com.shaneisrael.st.imgur.ImgurUploader;
import com.shaneisrael.st.prefs.Preferences;

public class Upload implements ImgurResponseListener
{
    private static final AnimatedTrayIcon animatedIcon = AnimatedTrayIcon.getDefaultIcon();

    private final BufferedImage image;
    private final boolean uploadToreddit;
    private final Save save;

    public Upload(BufferedImage image)
    {
        this(image, false);
    }

    public Upload(BufferedImage image, boolean uploadToReddit)
    {
        this.image = image;
        this.uploadToreddit = uploadToReddit;
        this.save = new Save();

        upload();
    }

    private void upload()
    {
        doBeforeUpload();
        ImgurUploader uploader = new ImgurUploader();
        uploader.upload(image, this);
    }

    private void doBeforeUpload()
    {
        if (OperatingSystem.isWindows())
        {
            new Thread(animatedIcon, "upload-animation").start();
        } else
        {
            Main.trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/uploadMac.png")).getImage());
        }
        Main.showNotification("uploading", STNotificationType.INFO);
    }

    private void doAfterUpload()
    {
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
    }

    @Override
    public void onImgurResponseSuccess(ImgurImage uploadedImage)
    {
        if (Preferences.getInstance().isAutoSaveEnabled())
        {
            save.saveUpload(image);
            new LinkDataSaver(uploadedImage.getLink(), uploadedImage.getDeleteLink(),
                "upload(" + Preferences.TOTAL_SAVED_UPLOADS + ")");
        }

        if (!uploadToreddit)
        {
            ClipboardUtilities.setClipboard(uploadedImage.getLink());
            Main.showNotification("upload-done", STNotificationType.SUCCESS);
        } else
        {
            Browser.openToReddit(uploadedImage.getLink());
            Main.showNotification("upload-done-reddit", STNotificationType.SUCCESS);
        }

        SoundNotifications.playDing();
        doAfterUpload();
    }

    @Override
    public void onImgurResponseFail(ImgurResponse response)
    {
        Main.showNotification("upload-failed", STNotificationType.ERROR);
        doAfterUpload();
    }

    public static Upload uploadFile(File imageFile, boolean uploadToReddit) throws IOException
    {
        return new Upload(ImageIO.read(imageFile), uploadToReddit);
    }
}
