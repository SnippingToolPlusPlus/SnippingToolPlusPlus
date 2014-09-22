package com.shaneisrael.st.ui.imageviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.shaneisrael.st.prefs.Preferences;

public class ImageViewerLinkBuilder
{
    private final ArrayList<ImageLinkPair> availableImages;
    private final HashMap<File, ImgurLinks> imageData;

    public ImageViewerLinkBuilder()
    {
        availableImages = new ArrayList<>();
        imageData = new HashMap<>();
        refresh();
    }

    private File getLinkFile()
    {
        File linkFile = new File(
            Preferences.getInstance().getCaptureDirectoryRoot() + "/Uploads/imgur_links.txt");
        return linkFile;
    }

    public ArrayList<ImageLinkPair> getImages()
    {
        return availableImages;
    }

    private void refresh()
    {
        loadImageData();
        loadImagesFromDirectory(new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/Uploads/"));
        loadImagesFromDirectory(new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/Captures/"));
        Collections.sort(availableImages, new Comparator<ImageLinkPair>()
        {
            @Override
            public int compare(ImageLinkPair a, ImageLinkPair b)
            {
                long timeA = a.getImageFile().lastModified();
                long timeB = b.getImageFile().lastModified();
                return Long.compare(timeA, timeB);
            }
        });
    }

    private void loadImagesFromDirectory(File rootDir)
    {
        File[] capturedImageFiles =
            rootDir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("png");
                }
            });
        for (File imageFile : capturedImageFiles)
        {
            if (imageData.containsKey(imageFile))
            {
                ImgurLinks linkData = imageData.get(imageFile);
                availableImages.add(new ImageLinkPair(imageFile, linkData.imageLink, linkData.deleteLink));
            } else
            {
                availableImages.add(new ImageLinkPair(imageFile));
            }
        }
    }

    private void loadImageData()
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(getLinkFile()));
            String line;
            String parsedLine[];
            int skip = 2;

            while ((line = reader.readLine()) != null)
            {
                if (skip == 0)
                {
                    parsedLine = line.split(" - ");
                    String imageName = parsedLine[0] + ".png";

                    File imageFile = new File(
                        Preferences.getInstance().getCaptureDirectoryRoot() + "/Uploads/" + imageName);
                    imageData.put(imageFile, new ImgurLinks(parsedLine[1], parsedLine[2]));
                } else
                {
                    skip--;
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ImgurLinks
    {
        public final String imageLink;
        public final String deleteLink;

        public ImgurLinks(String imageLink, String deleteLink)
        {
            this.imageLink = imageLink;
            this.deleteLink = deleteLink;
        }
    }
}
