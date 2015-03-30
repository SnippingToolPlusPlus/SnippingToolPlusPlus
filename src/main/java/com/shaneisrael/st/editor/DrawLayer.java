package com.shaneisrael.st.editor;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class DrawLayer
{
    private BufferedImage image;
    private Point location;
    public DrawLayer(BufferedImage img, Point location)
    {
        this.image = img;
        this.location = location;
    }
    public Point getLocation(){ return location;}
    public BufferedImage getImage(){return image;}
}
