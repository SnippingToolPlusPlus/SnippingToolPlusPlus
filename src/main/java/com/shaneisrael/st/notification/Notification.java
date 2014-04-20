package com.shaneisrael.st.notification;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.border.Border;

public abstract class Notification extends JFrame
{
    protected Component comp = null;
    protected JPanel panel;
    protected JLabel lblTitle;
    protected JTextPane lblMessage;
    protected Timer fadeOutTimer;
    protected Timer fadeInTimer;
    protected Timer slideOutTimer;
    protected Timer slideInTimer;
    protected Timer pauseTimer;
    protected Timer waitTimer;

    protected Point balloonPosition;

    protected Color backgroundColor = new Color(0f, 0f, 0f, 0f);
    protected Color titleColor = new Color(51, 204, 255);
    protected Color titleBackColor = new Color(0, 0, 0, 0);
    protected Color messageColor = Color.WHITE;
    protected Color messageBackColor = new Color(0, 0, 0, 0);
    protected Color sepColor = Color.WHITE;

    protected ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/icons/about_icon.png"));

    protected Shape frameShape = new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 20, 20);

    protected float currOpacity = 0;
    protected float finalOpacity = .60f;
    protected float fadeInRate = .02f;
    protected float fadeOutRate = .02f;
    protected int fadeOutTimerRate = 45;
    protected int fadeInTimerRate = 45;

    protected float slideOutTimerRate = 1f;
    protected float slideInTimerRate = 1f;

    protected int notification_pause_time = 5000; // time in ms, 5000 = 5
                                                  // seconds
    protected int wait_time = 0;

    protected int offScreenPosition;
    protected int margin = 10;
    protected int seperator_width = 150;
    protected int seperator_height = 1;

    protected Border panelBorder = null;

    protected boolean rounded_notification = false;

    protected JButton btnClose;

    public abstract void hideBalloon();

    public abstract void showBalloon(String title, String message);

    public abstract JPanel getPanel();

    /**
     * <li><b>initialize()</b></li>
     * <p>
     * Creates the notification box. Must always be called with each new Notification instance. This method must be called any time the default variables are
     * changed or else changes will not be made.
     */
    public abstract void initialize();

    abstract void reset();

    protected void setPosition()
    {
        String os = System.getProperty("os.name");
        Insets screenMax = getToolkit().getScreenInsets(getGraphicsConfiguration());

        if (comp == null) // if not tied to any component display at these
                          // default locations
        {
            offScreenPosition = (int) getToolkit().getScreenSize().getWidth();
            if (os.contains("Windows"))
            {
                /*
                 * Set it to the bottom right of the screen above the task tray;
                 */
                int taskbarSize = screenMax.bottom;
                int taskbarYlocation = getToolkit().getScreenSize().height - taskbarSize;
                balloonPosition = new Point(getToolkit().getScreenSize().width - getWidth() - margin, taskbarYlocation
                        - getHeight() - margin);
                setLocation(offScreenPosition, balloonPosition.y);
            } else if (os.contains("Mac"))
            {
                /*
                 * Set it to the top right of the screen below the task tray
                 */
                int taskbarSize = screenMax.top;
                int taskbarYlocation = taskbarSize;
                balloonPosition = new Point(getToolkit().getScreenSize().width - getWidth() - margin, taskbarYlocation
                        + margin);
                setLocation(offScreenPosition, balloonPosition.y);
            }
        } else
        // display at whereve the component is located on the screen
        {
            offScreenPosition = (int) (comp.getLocation().getX() + getWidth());
            balloonPosition = new Point(comp.getX(), comp.getY() - getHeight() - margin);
            setLocation(offScreenPosition, balloonPosition.y);
        }

    }

    /*
     * SET METHODS
     */
    public void setBackgroundColor(Color c)
    {
        backgroundColor = c;
    }

    public void setTitleColor(Color c)
    {
        titleColor = c;
    }

    public void setSubtextColor(Color c)
    {
        messageColor = c;
    }

    public void setSeperatorColor(Color c)
    {
        sepColor = c;
    }

    public void setSeperatorWidth(int width)
    {
        seperator_width = width;
    }

    public void setSeperatorHeight(int height)
    {
        seperator_height = height;
    }

    public void setNotificationIcon(ImageIcon i)
    {
        icon = i;
    }

    /**
     * setNotificationShape() Currently not working
     */
    public void setNotificationShape(Shape s)
    {
        frameShape = s;
    }

    public void setBeginningOpacity(float opacity)
    {
        currOpacity = opacity;
    }

    public void setEndingOpacity(float opacity)
    {
        finalOpacity = opacity;
    }

    public void setFadeInRate(float rate)
    {
        fadeInRate = rate;
    }

    public void setFadeOutRate(float rate)
    {
        fadeOutRate = rate;
    }

    public void setSlideOutRate(float rate)
    {
        slideOutTimerRate = rate;
    }

    public void setSlideInRate(float rate)
    {
        slideInTimerRate = rate;
    }

    public void setPauseTime(int ms)
    {
        notification_pause_time = ms;
    }

    public void setWaitTime(int ms)
    {
        wait_time = ms;
    }

    public void setBevel(int bevel)
    {
        margin = bevel;
    }

    public void setBorder(Border border)
    {
        panelBorder = border;
    }

    public void createRoundedNotification(boolean rounded)
    {
        rounded_notification = rounded;
    }

    /*
     * GET METHODS
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public Color getTitleColor()
    {
        return titleColor;
    }

    public Color getSubtextColor()
    {
        return messageColor;
    }

    public Color getSeperatorColor()
    {
        return sepColor;
    }

    public ImageIcon getNotificationIcon()
    {
        return icon;
    }

    public Shape getNotificationShape()
    {
        return frameShape;
    }

    public float getBeginningOpacity()
    {
        return currOpacity;
    }

    public float getEndingOpacity()
    {
        return finalOpacity;
    }

    public float getFadeInRate()
    {
        return fadeInRate;
    }

    public float getFadeOutRate()
    {
        return fadeOutRate;
    }

    public float getSlideOutRate()
    {
        return slideOutTimerRate;
    }

    public float getSlideInRate()
    {
        return slideInTimerRate;
    }

    public int getPauseTime()
    {
        return notification_pause_time;
    }

    public int getWaitTime()
    {
        return wait_time;
    }

    public int getBevel()
    {
        return margin;
    }

    public int getSeperatorWidth()
    {
        return seperator_width;
    }

    public int getSeperatorHeight()
    {
        return seperator_height;
    }

    public Border getBorder()
    {
        return panelBorder;
    }
}
