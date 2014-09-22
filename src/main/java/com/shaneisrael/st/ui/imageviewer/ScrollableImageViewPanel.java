package com.shaneisrael.st.ui.imageviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ScrollableImageViewPanel extends JPanel
{
    private static final long serialVersionUID = -6748235353933243438L;
    private BufferedImage image;
    private JPanel imagePanel;
    private JScrollPane scrollpane;

    public ScrollableImageViewPanel()
    {
        imagePanel = new JPanel()
        {
            private static final long serialVersionUID = 5103768960233011538L;

            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (image != null)
                {
                    g2d.drawImage(image, 0, 0, null);
                }
            }
        };
        scrollpane = new JScrollPane(imagePanel);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpane.getVerticalScrollBar().setUnitIncrement(16);
        scrollpane.getHorizontalScrollBar().setUnitIncrement(16);
        setLayout(new BorderLayout());
        add(scrollpane, BorderLayout.CENTER);
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
        imagePanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        scrollpane.setViewportView(imagePanel);
        repaint();
    }
}
