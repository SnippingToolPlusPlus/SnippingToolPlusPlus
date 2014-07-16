package com.shaneisrael.st;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.shaneisrael.st.utilities.ImagePanel;
import com.shaneisrael.st.utilities.version.Version;

public class AboutFrame
{
    private JFrame aboutFrame;
    private ImagePanel logoPanel;

    public AboutFrame()
    {
        initialize();
    }

    private void initialize()
    {
        aboutFrame = new JFrame();
        aboutFrame.setIconImage(
            Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/images/st-icon.png")
                )
            );
        aboutFrame.setTitle("Snipping Tool++ v" + Version.getCurrentRunningVersion().getVersionString());
        aboutFrame.setResizable(false);
        aboutFrame.setAlwaysOnTop(true);
        aboutFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        logoPanel = new ImagePanel("/images/logo-background.png");
        aboutFrame.getContentPane().add(logoPanel);

        aboutFrame.pack();
        aboutFrame.setLocationRelativeTo(null);
        aboutFrame.setVisible(true);
    }
}
