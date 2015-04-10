package com.shaneisrael.st.ui.imageviewer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.utilities.ProgressBarDialog;
import com.shaneisrael.st.utilities.database.DBConnection;
import com.shaneisrael.st.utilities.database.DBUniqueKey;

public class ImageViewerLinkBuilder
{
    private final ArrayList<ImageLinkPair> availableImages;
    private final ArrayList<File> cloudFiles;
    private final HashMap<File, ImgurLinks> imageData;
    private ProgressBarDialog dialog = null;

    public ImageViewerLinkBuilder(boolean local)
    {
        availableImages = new ArrayList<>();
        imageData = new HashMap<>();
        cloudFiles = new ArrayList<File>();
        refresh(local);
    }

    public boolean hasCloudFiles()
    {
        return !cloudFiles.isEmpty();
    }

    public ArrayList<File> getCloudFiles()
    {
        return cloudFiles;
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

    private void refresh(boolean local)
    {
        if (local)
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
        else
        {
            loadCloudImageData();
            loadImagesFromCloud();
        }
    }

    protected void loadImagesFromCloud()
    {
        for (File imageFile : cloudFiles)
        {
            if (imageData.containsKey(imageFile))
            {
                ImgurLinks linkData = imageData.get(imageFile);
                availableImages.add(new ImageLinkPair(imageFile, linkData.imageLink, linkData.deleteLink));
            }
            else
            {
                availableImages.add(new ImageLinkPair(imageFile));
            }
        }
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

    private void loadCloudImageData()
    {
        try
        {
            File tempDir = new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/TempHistory");
            if (!tempDir.exists())
                tempDir.mkdirs();

            tempDir.deleteOnExit();

            PreparedStatement statement;
            Connection connect;
            ResultSet result;
            String key1 = Preferences.getInstance().getUniqueKey1();
            String key2 = Preferences.getInstance().getUniqueKey2();
            if (DBUniqueKey.validate(key1, key2))
            {
                String id = DBUniqueKey.getUniqueKeyID(key1, key2);

                connect = DBConnection.getConnection();
                statement = connect.prepareStatement(
                    "SELECT upload_link, delete_link, timestamp FROM upload_history WHERE rkid=?");
                statement.setString(1, id);

                result = statement.executeQuery();

                int poolSize = 15;
                ExecutorService pool = Executors.newFixedThreadPool(poolSize);

                int size = 0;
                while (result.next())
                {
                    size++;

                    String uplink = result.getString("upload_link");
                    String dellink = result.getString("delete_link");
                    pool.submit(new DownloadTask(uplink, dellink));
                }
                dialog = ProgressBarDialog.createNewInstance("Downloading history...", size);

                pool.shutdown();
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                result.close();
                statement.close();
                connect.close();
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    private class DownloadTask implements Runnable
    {

        private String uplink;
        private final String dellink;

        public DownloadTask(String uplink, String dellink)
        {
            this.uplink = uplink;
            this.dellink = dellink;
        }

        @Override
        public void run()
        {
            // surround with try-catch if downloadFile() throws something
            downloadFile(uplink, dellink);
        }

        private void downloadFile(String uplink2, String dellink2)
        {
            BufferedImage img = null;
            URL url = null;
            String[] split = uplink.split("/");
            try
            {
                url = new URL(uplink);
                img = ImageIO.read(url);
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            File imageFile = new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/TempHistory/" + split[3]);
            imageFile.deleteOnExit();

            try
            {
                ImageIO.write(img, "png", imageFile);
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cloudFiles.add(imageFile);
            imageData.put(imageFile,
                new ImgurLinks(uplink, dellink));

            if (dialog != null)
                dialog.updateProgress();

        }

    }
}
