package com.shaneisrael.st.utilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ViewerPanel extends JPanel
{
	private BufferedImage image;

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (image != null)
			g2d.drawImage(image, 0, 0, null);

		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}

	public void setImage(BufferedImage image)
	{
		this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		this.image = image;
	}
}
