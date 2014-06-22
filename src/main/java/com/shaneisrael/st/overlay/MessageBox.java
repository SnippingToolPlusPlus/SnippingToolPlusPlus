package com.shaneisrael.st.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class MessageBox
{
    private List<MultiColorText> messages;
    private Font font;
    private int x;
    private int y;

    private int padTop = 0;
    private int padLeft = 5;
    private int padRight = 5;
    private int padBottom = 5;

    public MessageBox(List<MultiColorText> messages, Font font)
    {
        this.messages = messages;
        this.font = font;
    }

    public Rectangle getBounds(Graphics2D g2d)
    {
        int width = 0;
        int height = 0;
        for (MultiColorText text : messages)
        {
            g2d.setFont(font);
            Rectangle2D textBounds = text.getBounds(g2d, font);
            if (textBounds.getWidth() > width)
            {
                width = (int) textBounds.getWidth();
            }
            height += (int) textBounds.getHeight();
        }
        return new Rectangle(0, 0, width, height);
    }

    public Rectangle2D getBoundsWithPadding(Graphics2D g2d)
    {
        Rectangle normalBounds = getBounds(g2d);
        return new Rectangle(
            normalBounds.x - padLeft,
            normalBounds.y - padTop,
            normalBounds.width + padLeft + padRight,
            normalBounds.height + padTop + padBottom);
    }

    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2d)
    {
        Rectangle2D boxBounds = getBounds(g2d);
        int width = (int) boxBounds.getWidth();
        int height = (int) boxBounds.getHeight();

        drawBorder(g2d, width, height);
        drawMessages(g2d, width, height);
    }

    private void drawMessages(Graphics2D g2d, int width, int height)
    {
        int yOffset = (int) messages.get(0).getBounds(g2d, font).getHeight();
        for (MultiColorText message : messages)
        {
            message.draw(g2d, font, x, y + yOffset);
            yOffset += message.getBounds(g2d, font).getHeight();
        }
    }

    private void drawBorder(Graphics2D g2d, int width, int height)
    {
        int paddedX = x - padLeft;
        int paddedY = y - padTop;
        int paddedWidth = width + padLeft + padRight;
        int paddedHeight = height + padTop + padBottom;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(paddedX, paddedY, paddedWidth, paddedHeight);
        g2d.setColor(Color.white);
        g2d.drawRect(paddedX, paddedY, paddedWidth, paddedHeight);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (MultiColorText text : messages)
        {
            builder.append(text.getSimpleString() + "\n");
        }
        return builder.toString();
    }

}
