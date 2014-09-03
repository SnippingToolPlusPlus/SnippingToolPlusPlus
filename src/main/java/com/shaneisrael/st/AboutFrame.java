package com.shaneisrael.st;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.shaneisrael.st.ui.AboutPanel;

public class AboutFrame
{
    private JFrame aboutFrame;
    private AboutPanel logoPanel;

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
        aboutFrame.setTitle("Snipping Tool++");
        aboutFrame.setResizable(false);
        aboutFrame.setAlwaysOnTop(true);
        aboutFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        logoPanel = new AboutPanel();
        aboutFrame.getContentPane().add(logoPanel);

        aboutFrame.pack();
        aboutFrame.setLocationRelativeTo(null);
        aboutFrame.setVisible(true);
    }
}
