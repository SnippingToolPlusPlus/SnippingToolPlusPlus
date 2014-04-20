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
	public static boolean ACTIVE = false;
	private Overlay overlayPanel;
	private String os = System.getProperty("os.name");

	public OverlayFrame()
	{
		ACTIVE = true; // the overlay is currently opened.

		setUndecorated(true);
		if (os.indexOf("Mac") >= 0)
		{
			this.setBounds(getScreenSize());

		} else
		{ // Windows
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
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				
			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
				ACTIVE = false;
				System.gc();

			}

			@Override
			public void windowActivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	private Rectangle getScreenSize()
	{
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
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
