package com.shaneisrael.st.notification;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.shaneisrael.st.data.Logger;

/**
 * 
 * @author Shane This is the notifications windows, what you see.
 */
public class STNotificationWindow extends JComponent
{
    private String texturePath = STTheme.getRootPath() + "/window/";
    private String typePath = STTheme.getRootPath() + "/type/";
    private String titlePath = STTheme.getRootPath() + "/titles/";

    private BufferedImage texture;
    private BufferedImage type;
    private BufferedImage titleImage;

    private String title;

    public STNotificationWindow(STNotificationType t, String title)
    {
        this.title = title;

        texturePath += "window.png";
        switch (t)
        {
        case INFO:
            typePath += "normal.png";
            break;
        case ERROR:
            typePath += "error.png";
            break;
        case WARNING:
            typePath += "warning.png";
            break;
        case MESSAGE:
            typePath += "normal.png";
            break;
        case SUCCESS:
            typePath += "success.png";
            break;
        }

       
        try
        {
            type = ImageIO.read(this.getClass().getResourceAsStream(typePath));
            titleImage = ImageIO.read(this.getClass().getResourceAsStream(titlePath + title + ".png"));
            texture = ImageIO.read(this.getClass().getResourceAsStream(texturePath));
        } catch (IOException e)
        {
            Logger.Log(e);
            e.printStackTrace();
        }

        this.setOpaque(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setSize(271, 179);
        this.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        if (title == null)
        {
            g2d.drawImage(texture, 0, 0, this);
            g2d.drawImage(type, 0, 0, this);
        }
        else
        {
            g2d.drawImage(texture, 0, 0, this);
            g2d.drawImage(type, 0, 0, this);
            g2d.drawImage(titleImage, 0, 0, this);
        }
        g2d.dispose();
    }
}
