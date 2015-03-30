package com.shaneisrael.st.editor;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ColorSelectionPanelOLD extends JPanel
{
    private static final long serialVersionUID = 3147366598069033090L;
    private Color color = Color.red;

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(getColor());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        this.repaint();
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color col)
    {
        this.color = col;
        this.repaint();
    }

}
