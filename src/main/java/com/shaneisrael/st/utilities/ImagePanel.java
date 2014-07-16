package com.shaneisrael.st.utilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
    private static final long serialVersionUID = -4052593806526672895L;
    private BufferedImage image;

    public ImagePanel(String imagePath)
    {
        try
        {
            image = ImageIO.read(getClass().getResource(imagePath));
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
