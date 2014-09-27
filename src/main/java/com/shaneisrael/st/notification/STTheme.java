package com.shaneisrael.st.notification;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;

/**
 * 
 * @author Shane
 * 
 *         This class will be used to pull the root directory from a preferences file. This will need to be changed by the user.
 * 
 *         For now it will simply point to the default location
 * 
 *         Everything is set to default values. when this class is finished, it will pull the values from a config file located in the directory.
 * 
 */
public class STTheme
{
    private static String themePath = "/theme/cloudy";
    private static int width;
    private static int height;

    public static void configure()
    {
        /**
         * Loads the config file
         */

        String line;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(STTheme.class.getResourceAsStream(getRootPath()
                + "/config.txt")));
            while ((line = reader.readLine()) != null)
            {
                String split[] = line.split("=");

                if (split[0].equals("width"))
                    setWidth(Integer.parseInt(split[1]));
                else if (split[0].equals("height"))
                    setHeight(Integer.parseInt(split[1]));
            }
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getRootPath()
    {
        return themePath;
    }

    public static void setThemePath(String path)
    {
        themePath = path;
        configure();
    }

    public static int getHeight()
    {
        return height;
    }

    public static int getWidth()
    {
        return width;
    }

    private static void setWidth(int w)
    {
        STTheme.width = w;
    }

    private static void setHeight(int h)
    {
        STTheme.height = h;
    }

    public static int getScreenLocationY(JFrame w)
    {
        //        Insets screenMax = Toolkit.getDefaultToolkit().getScreenInsets(w.getGraphicsConfiguration());
        //
        //        // Set it to the bottom right of the screen below the task tray;
        //        int taskbarSize = screenMax.bottom;
        //        int taskbarYlocation = Toolkit.getDefaultToolkit().getScreenSize().height - taskbarSize;

        //       return taskbarYlocation - getHeight();

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        return screenHeight;
    }

    public static int getScreenLocationX(JFrame w)
    {
        return Toolkit.getDefaultToolkit().getScreenSize().width - getWidth() - 15;
    }

    public static int getTaskbarHeight()
    {
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle taskSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        int taskHeight = scrnSize.height - taskSize.height;
        return taskHeight;
    }

    public static int getTravelDistance()
    {
        return getTaskbarHeight() + getHeight();
    }

    public static int getTravelLocation()
    {
        int scrnHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        return scrnHeight - getTravelDistance();
    }

}
