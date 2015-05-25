package com.shaneisrael.st.utilities;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.overlay.ScreenBounds;

public class CaptureScreen
{
    private Rectangle screenRectangle;
    private ScreenBounds bounds = new ScreenBounds();

    public BufferedImage getScreenCapture()
    {
        screenRectangle = bounds.getBounds();
        Robot robot = null;
        try
        {
            robot = new Robot();
        } catch (AWTException e)
        {
            Logger.Log(e);
            e.printStackTrace();
        }
        return robot.createScreenCapture(screenRectangle);
    }
}