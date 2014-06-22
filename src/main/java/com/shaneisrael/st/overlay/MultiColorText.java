package com.shaneisrael.st.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class MultiColorText
{
    private LinkedHashMap<String, Color> textColorInfo;

    public MultiColorText(LinkedHashMap<String, Color> textColorInfo)
    {
        this.textColorInfo = textColorInfo;
    }

    public String getSimpleString()
    {
        StringBuilder simpleString = new StringBuilder();
        for (Entry<String, Color> piece : textColorInfo.entrySet())
        {
            simpleString.append(piece.getKey());
        }
        return simpleString.toString();
    }

    public void draw(Graphics2D g2d, Font font, int x, int y)
    {
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int xoffset = 0;
        for (Entry<String, Color> piece : textColorInfo.entrySet())
        {
            String text = piece.getKey();
            Color textColor = piece.getValue();
            g2d.setColor(textColor);
            Rectangle2D textBounds = fm.getStringBounds(text, g2d);
            g2d.drawString(text, x + xoffset, y);
            xoffset += textBounds.getWidth();
        }

    }

    public Rectangle2D getBounds(Graphics2D g2d, Font font)
    {
        FontMetrics fm = g2d.getFontMetrics();
        return fm.getStringBounds(getSimpleString(), g2d);
    }

    @Override
    public String toString()
    {
        return getSimpleString();
    }
}
