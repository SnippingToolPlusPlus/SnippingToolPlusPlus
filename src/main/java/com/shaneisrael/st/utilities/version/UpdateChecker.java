package com.shaneisrael.st.utilities.version;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.shaneisrael.st.notification.Notification;
import com.shaneisrael.st.notification.SlidingNotification;
import com.shaneisrael.st.utilities.OpenBrowser;

public class UpdateChecker implements VersionResponseListener
{
    private final Version currentVersion;
    private final Notification updateNotification;
    private final LatestVersionChecker versionChecker;
    private JButton updateButton;

    public UpdateChecker()
    {
        currentVersion = Version.getCurrentRunningVersion();
        updateNotification = new SlidingNotification(null);
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
        updateButton = new JButton("Update Now");
        updateButton.setBounds(54, 44, 95, 25);
        updateButton.setFocusable(false);
        updateButton.setOpaque(false);
        updateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateNotification.hideBalloon();

            }
        });
        updateNotification.add(updateButton);
        updateNotification.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0, 200), 1, false)); //give it a green border
        updateNotification.setAlwaysOnTop(true);
        updateNotification.setWaitTime(10000); //wait 10 seconds before displaying
        updateNotification.setPauseTime(300000); //wait on screen for 5 mins or until user manually closes it.
        updateNotification.setTitleColor(Color.green);
        updateNotification.setSeperatorWidth(140);
        updateNotification.getPanel().setToolTipText("Snipping Tool++ Notification");
        updateNotification.setBounds(updateNotification.getX(), updateNotification.getY(), 200,
            updateNotification.getHeight());

        /*
         * Creates the notification box. Must always be called with each new Notification instance. 
         * This method must be called any time the default variables are changed or else changes will not be made.
         */
        updateNotification.initialize();

    }

    @Override
    public void onVersionResponseSuccess(final Version latestVersion)
    {
        System.out.println("Latest version is " + latestVersion.getVersionStringWithName());

        if (!Version.isDebug() && currentVersion.compareTo(latestVersion) < 0)
        {
            updateButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        OpenBrowser.open(new URI(latestVersion.getDownloadLocation()));
                    } catch (URISyntaxException e1)
                    {
                        try
                        {
                            OpenBrowser.open(new URI("http://snippingtoolpluspl.us/"));
                        } catch (URISyntaxException e2)
                        {
                            System.out.println("Could not get latest version information.");
                        }
                    }
                }
            });
            updateNotification.getPanel().setToolTipText(latestVersion.getVersionStringWithName());
            updateNotification.showBalloon("Update Available!", "");
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