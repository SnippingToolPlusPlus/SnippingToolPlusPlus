package com.shaneisrael.st.overlay;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class ScreenBounds
{

	public Rectangle getBounds()
	{
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		Rectangle virtualBounds = new Rectangle();

		for (GraphicsDevice device : gs)
		{
			GraphicsConfiguration[] gc = device.getConfigurations();

			for (int i = 0; i < gc.length; i++)
			{
				virtualBounds = virtualBounds.union(gc[i].getBounds());
			}
		}
		return virtualBounds;
	}
}
