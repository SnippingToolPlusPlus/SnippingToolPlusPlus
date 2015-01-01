package com.shaneisrael.st.utilities.version;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.shaneisrael.st.notification.NotificationManager;
import com.shaneisrael.st.notification.STNotification;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.utilities.Browser;

public class UpdateChecker implements VersionResponseListener
{
    private final Version currentVersion;
    private Version latestVersion;
    private STNotification updateNotification;
    private final LatestVersionChecker versionChecker;

    public UpdateChecker()
    {
        currentVersion = Version.getCurrentRunningVersion();
        initilizeNotification();

        versionChecker = new LatestVersionChecker();
    }

    /**
     * Asynchronously checks for updates and alerts the user via the Notification system if an update is available.
     * 
     */
    public void checkForUpdates()
    {
        versionChecker.fetchLatestVersion(this);
    }

    private void initilizeNotification()
    {
        updateNotification = new STNotification("update-now", STNotificationType.WARNING);
        updateNotification.addMouseListener(new MouseListener()
        {

            @Override
            public void mouseReleased(MouseEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                try
                {
                    Browser.open(new URI(latestVersion.getDownloadLocation()));
                } catch (URISyntaxException e1)
                {
                    try
                    {
                        
                        Browser.open(new URI("http://snippingtoolpluspl.us/"));
                    } catch (URISyntaxException e2)
                    {
                        System.out.println("Could not get latest version information.");
                    }
                }
                
                updateNotification.dispose();

            }
        });
        updateNotification.setPauseTime(90000); //Wait 1.5 mins

    }

    @Override
    public void onVersionResponseSuccess(final Version latestVersion)
    {
        this.latestVersion = latestVersion;
        System.out.println("Latest version is " + latestVersion.getVersionStringWithName());

        if (!Version.isDebug() && currentVersion.compareTo(latestVersion) < 0)
        {
            NotificationManager.getInstance().showNotification(updateNotification);
        } else
        {
            System.out.println("You are running the latest version.");
        }
    }

    @Override
    public void onVersionResponseFailure(String reason)
    {
        System.out.println("Could not find latest version information because: " + reason);
    }
}