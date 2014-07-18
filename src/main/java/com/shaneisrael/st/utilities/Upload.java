package com.shaneisrael.st.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

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
        Main.displayInfoMessage("Uploading...", "Link will be available shortly");
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
            Main.displayInfoMessage("Upload Successful!", "Link has been copied to your clipboard");
        } else
        {
            Browser.openToReddit(uploadedImage.getLink());
            Main.displayInfoMessage("Upload Successful!", "Submitting link to Reddit");
        }

        SoundNotifications.playDing();
        doAfterUpload();
    }

    @Override
    public void onImgurResponseFail(ImgurResponse response)
    {
        Main.displayErrorMessage("Upload Failed!", "An unexpected error has occurred");
        doAfterUpload();
    }

    public static Upload uploadFile(File imageFile, boolean uploadToReddit) throws IOException
    {
        return new Upload(ImageIO.read(imageFile), uploadToReddit);
    }
}
