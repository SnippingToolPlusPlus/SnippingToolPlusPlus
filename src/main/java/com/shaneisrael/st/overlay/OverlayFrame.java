package com.shaneisrael.st.overlay;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JRootPane;

public class OverlayFrame extends JFrame
{
    private static final long serialVersionUID = 4640312842620083014L;
    public static boolean IsActive = false;
    private Overlay overlayPanel;
    private String os = System.getProperty("os.name");

    public OverlayFrame()
    {
        IsActive = true; // the overlay is currently opened.

        setUndecorated(true);
        if (os.indexOf("Mac") >= 0)
        {
            this.setBounds(getScreenSize());

        } else
        {
            getRootPane().setWindowDecorationStyle(JRootPane.NONE);
            this.setBounds(getScreenSize());
        }
        overlayPanel = new Overlay(this);
        this.add(overlayPanel);
        this.setVisible(true);
        this.addWindowListener(new WindowListener()
        {

            @Override
            public void windowOpened(WindowEvent arg0)
            {
            }

            @Override
            public void windowIconified(WindowEvent arg0)
            {
            }

            @Override
            public void windowDeiconified(WindowEvent arg0)
            {
            }

            @Override
            public void windowDeactivated(WindowEvent arg0)
            {
            }

            @Override
            public void windowClosing(WindowEvent arg0)
            {

            }

            @Override
            public void windowClosed(WindowEvent arg0)
            {
                IsActive = false;
                System.gc();
            }

            @Override
            public void windowActivated(WindowEvent arg0)
            {
            }
        });
    }

    private Rectangle getScreenSize()
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        Rectangle bounds = new Rectangle();

        for (GraphicsDevice device : devices)
        {
            GraphicsConfiguration[] gc = device.getConfigurations();
            for (int i = 0; i < gc.length; i++)
            {
                bounds = bounds.union(gc[i].getBounds());
            }
        }

        return bounds;
    }

    public void setMode(int mode)
    {
        overlayPanel.setMode(mode);
    }
}
