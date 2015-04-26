package com.shaneisrael.st.ui;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.utilities.version.Version;

public class AboutFrame extends JFrame
{
    private static final long serialVersionUID = 3137296899967294809L;
    private AboutPanel logoPanel;

    public AboutFrame()
    {
        initialize();
    }

    private void initialize()
    {
        this.setIconImage(
            Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/images/st-icon.png")
                )
            );
        this.setTitle("About");
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        logoPanel = new AboutPanel();
        this.getContentPane().add(logoPanel);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
