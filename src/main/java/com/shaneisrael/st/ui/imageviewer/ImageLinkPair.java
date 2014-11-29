package com.shaneisrael.st.ui.imageviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLinkPair
{
    private final File imageFile;

    private final String imageLink;

    private final String deleteLink;

    public ImageLinkPair(File imageFile, String imageLink, String deleteLink)
    {
        this.imageFile = imageFile;
        this.imageLink = imageLink;
        this.deleteLink = deleteLink;
    }

    public ImageLinkPair(File imageFile)
    {
        this(imageFile, null, null);
    }

    public File getImageFile()
    {
        return imageFile;
    }

    public BufferedImage getImage() throws IOException
    {
        return ImageIO.read(getImageFile());
    }

    public String getImageLink()
    {
        return imageLink;
    }

    public String getDeleteLink()
    {
        return deleteLink;
    }

    public boolean hasImgurInfo()
    {
        return getImageLink() != null && getDeleteLink() != null;
    }

    @Override
    public String toString()
    {
        if(getImageFile().getName() != null)
            return String.format("%s", getImageFile().getName());
        return "Error";
    }
}
