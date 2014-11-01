package com.shaneisrael.st.utilities;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.prefs.Preferences;

public class Save
{
    private String location;
    private File savLoc;

    public Save()
    {
        location = Preferences.getInstance().getCaptureDirectoryRoot();
    }

    public void save(BufferedImage img)
    {
        int index = 0;
        savLoc = new File(location + "/Captures/capture.png");

        do
        {
            index++;
            savLoc = new File(location + "/Captures/capture(" + index + ").png");
        } while (savLoc.exists());

        try
        {
            ImageIO.write(img, "png", savLoc);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Main.showNotification("saved", STNotificationType.SUCCESS);
    }

    public void saveUpload(BufferedImage img)
    {
        int index = 0;
        img = ImageUtilities.compressImage(img, Preferences.getInstance().getUploadQuality());
        savLoc = new File(location + "/Uploads/upload.png");

        do
        {
            index++;
            savLoc = new File(location + "/Uploads/upload(" + index + ").png");
        } while (savLoc.exists());

        Preferences.TOTAL_SAVED_UPLOADS = index;
        try
        {
            ImageIO.write(img, "png", savLoc);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
