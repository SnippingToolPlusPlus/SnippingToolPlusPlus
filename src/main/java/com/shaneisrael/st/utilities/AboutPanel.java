package com.shaneisrael.st.utilities;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.shaneisrael.st.Config;
import com.shaneisrael.st.utilities.version.LatestVersionChecker;
import com.shaneisrael.st.utilities.version.Version;
import com.shaneisrael.st.utilities.version.VersionResponseListener;

public class AboutPanel extends ImagePanel implements MouseListener, VersionResponseListener
{
    private static final long serialVersionUID = 5797146603275408767L;
    private final Font aboutFont;
    private LatestVersionChecker versionChecker;
    private Graphics2D g2d;

    private Version currentVersion;
    private Version latestVersion;
    private String message;

    public AboutPanel()
    {
        super("/images/logo-background.png");

        aboutFont = new Font("Segoe UI", Font.PLAIN, 16);
        addMouseListener(this);

        currentVersion = Version.getCurrentRunningVersion();

        if (Version.isDebug())
        {
            message = "SnippingTool++ development mode";
        } else
        {
            versionChecker = new LatestVersionChecker();
            versionChecker.fetchLatestVersion(this);
            message = "...";
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(aboutFont);
        drawVersionInfo();
    }

    private void drawVersionInfo()
    {
        int bottomMargin = 5;
        int leftMargin = 2;
        g2d.drawString(message, 0 + leftMargin, getHeight() - bottomMargin);
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
        repaint();
        if (currentVersion.isUpToDate(latestVersion))
        {
            Browser.open(Config.WEBSITE_URL);
        } else
        {
            System.out.println("Attempting to download from " + latestVersion.getDownloadLocation());
            Browser.open(latestVersion.getDownloadLocation());
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent arg0)
    {
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
    }

    @Override
    public void onVersionResponseSuccess(Version latestVersion)
    {
        this.latestVersion = latestVersion;
        if (currentVersion.isUpToDate(latestVersion))
        {
            setToolTipText("Click to go to the Snipping Tool++ website");
            message = String.format("v%s %s", currentVersion.getVersionString(), "Snipping Tool++ is up to date");
        } else
        {
            message = "Click to update to v" + latestVersion.getVersionStringWithName();
            setToolTipText(message);
        }
        repaint();
    }

    @Override
    public void onVersionResponseFailure(String reason)
    {
        message = "cannot reach server";
        repaint();
    }
}
