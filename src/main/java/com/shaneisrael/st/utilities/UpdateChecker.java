package com.shaneisrael.st.utilities;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.jsoup.Jsoup;

import com.shaneisrael.st.data.Preferences;
import com.shaneisrael.st.notification.Notification;
import com.shaneisrael.st.notification.SlidingNotification;

/*
 * This class just checks my tools webpage and notifies the user if there is a new update to the program.
 */
public class UpdateChecker
{
    private static String htmlContent = "";
    static String updateSite = "http://www.snippingtoolplusplus.co.nf";
    static int latestVersion;
    static int currentVersion = Integer.parseInt(Preferences.getVersion().replace(".", ""));

    private static Notification updateNotification = new SlidingNotification(null);
    private JButton updateButton;

    public UpdateChecker()
    {
        initilizeNotification();
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
                try
                {
                    OpenBrowser.open(new URI("http://www.snippingtoolplusplus.co.nf"));
                } catch (URISyntaxException e1)
                {
                    e1.printStackTrace();
                }
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

    public void checkForUpdates()
    {
        try
        {
            htmlContent = Jsoup.connect(updateSite).timeout(30000).get().html();
            BufferedReader bufReader = new BufferedReader(new StringReader(htmlContent));

            String line = null;

            try
            {
                while ((line = bufReader.readLine()) != null)
                {
                    if (line.contains("Download"))
                    {
                        htmlContent = line;
                        break;
                    }
                }

                String[] split = htmlContent.split(">Download");
                split = split[1].split("</a>");
                System.out.println(split[0]);
                latestVersion = Integer.parseInt(split[0].replace(" ", "").replace(".", ""));

                if (currentVersion < latestVersion)
                {
                    updateNotification.showBalloon("Update Available!", "");
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
