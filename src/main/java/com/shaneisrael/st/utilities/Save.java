package com.shaneisrael.st.utilities;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.Preferences;

public class Save
{
    private int total_captures = 0;
    private String location;
    private File savLoc;

    public Save()
    {
        location = Preferences.DEFAULT_CAPTURE_DIR;
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

        total_captures = index;
        try
        {
            ImageIO.write(img, "png", savLoc);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Main.displayInfoMessage("Saved!", "Click here to open file location.");
    }

    public void saveUpload(BufferedImage img)
    {
        int index = 0;
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
