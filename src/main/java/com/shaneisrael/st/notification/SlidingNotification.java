package com.shaneisrael.st.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import com.shaneisrael.st.data.OperatingSystem;

public class SlidingNotification extends Notification
{
    private static final long serialVersionUID = 6993558070289918427L;

    /**
     * Create a new SlidingNotification.
     */
    public SlidingNotification(Component c)
    {
        this.setSize(300, 75);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        this.setAlwaysOnTop(false);
        this.comp = c;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initialize()
    {
        if(OperatingSystem.isWindows()) {
            this.setType(Type.UTILITY);
        }
        panel = new JPanel();
        panel.setBorder(panelBorder);
        panel.setBackground(new Color(0, 0, 0, 0));
        this.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        lblTitle = new JLabel("Uploading...");
        lblTitle.setBounds(30, 10, 240, 27);
        lblTitle.setForeground(titleColor);
        lblTitle.setBackground(titleBackColor);
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 15));
        panel.add(lblTitle);

        lblMessage = new JTextPane();
        lblMessage.setFont(new Font("Verdana", Font.BOLD, 12));
        lblMessage.setForeground(messageColor);
        lblMessage.setBackground(messageBackColor);
        lblMessage.setBounds(28, 35, 260, 45);
        lblMessage.setEditable(false);

        panel.add(lblMessage);

        btnClose = new JButton("");
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusable(false);
        Border emptyBorder = BorderFactory.createEmptyBorder();
        btnClose.setBorder(emptyBorder);

        btnClose.setSelectedIcon(new ImageIcon(Notification.class.getResource("/images/icons/exit_d_pressed.png")));
        btnClose.setIcon(new ImageIcon(Notification.class.getResource("/images/icons/exit_d_normal.png")));
        btnClose.setRolloverIcon(new ImageIcon(Notification.class.getResource("/images/icons/exit_d_rollover.png")));
        if (rounded_notification)
            btnClose.setBounds(getWidth() - 28, 5, 20, 23);
        else
            btnClose.setBounds(getWidth() - 56, 2, 51, 23);
        btnClose.setHorizontalAlignment(SwingConstants.RIGHT);
        btnClose.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                hideBalloon();
            }
        });
        panel.add(btnClose);

        JLabel imageLbl = new JLabel("");
        imageLbl.setIcon(icon);
        if (rounded_notification)
        {
            imageLbl.setBounds(10, 5, 46, 14);
        } else
        {
            imageLbl.setBounds(5, 5, 46, 14);
        }

        panel.add(imageLbl);

        JSeparator separator = new JSeparator();
        separator.setForeground(sepColor);
        separator.setBounds(32, 36, seperator_width, seperator_height);
        panel.add(separator);

        this.fadeInTimer = new Timer(fadeInTimerRate, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                currOpacity += fadeInRate;
                if (currOpacity <= finalOpacity)
                {
                    int o = (int) (currOpacity * 255);
                    setBackground(new Color(backgroundColor.getRed(), backgroundColor.getGreen(),
                            backgroundColor.getBlue(), o));
                    lblTitle.setForeground(new Color((titleColor.getRed()), (titleColor.getGreen()), (titleColor
                            .getBlue()), o));
                    lblMessage.setForeground(new Color(messageColor.getRed(), messageColor.getGreen(), messageColor
                            .getBlue(), o));
                    getContentPane().repaint();
                } else
                {
                    currOpacity = finalOpacity;
                    fadeInTimer.stop();
                }
            }

        });

        this.fadeOutTimer = new Timer(fadeOutTimerRate, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                currOpacity -= fadeOutRate;
                if (currOpacity >= 0f)
                {
                    int o = (int) (currOpacity * 255);
                    setBackground(new Color(backgroundColor.getRed(), backgroundColor.getGreen(),
                            backgroundColor.getBlue(), o));
                    lblTitle.setForeground(new Color((titleColor.getRed()), (titleColor.getGreen()), (titleColor
                            .getBlue()), o));
                    lblMessage.setForeground(new Color(messageColor.getRed(), messageColor.getGreen(), messageColor
                            .getBlue(), o));
                    getContentPane().repaint();
                } else
                {
                    currOpacity = 0f;
                    fadeInTimer.stop();
                    setVisible(false);
                }
            }

        });
        this.slideOutTimer = new Timer((int) slideOutTimerRate, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setLocation(getX() + 1, getY());
                if (getX() > getToolkit().getScreenSize().getWidth())
                {
                    slideOutTimer.stop();
                }
            }

        });
        this.slideInTimer = new Timer((int) slideInTimerRate, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (getX() >= balloonPosition.x)
                {
                    setLocation(offScreenPosition -= 1, getY());
                } else
                {
                    setLocation(balloonPosition);
                    slideInTimer.stop();
                }
            }

        });
        this.pauseTimer = new Timer(notificationPauseTimeInMs, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                hideBalloon();
            }

        });
        this.waitTimer = new Timer(wait_time, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                start();
            }
        });
        setFrameShape();
    }

    @Override
    public void hideBalloon()
    {
        /*
         * Starts fading the popup
         */
        fadeOutTimer.setRepeats(true);
        fadeOutTimer.start();
        slideOutTimer.setRepeats(true);
        slideOutTimer.start();
        pauseTimer.stop();
        waitTimer.stop();

    }

    @Override
    public void showBalloon(String title, String message)
    {
        reset();
        lblTitle.setText(title);
        lblMessage.setText(message);

        waitTimer.setRepeats(false);
        waitTimer.start();
    }

    private void start()
    {
        this.fadeInTimer.setRepeats(true);
        this.fadeInTimer.start();
        this.slideInTimer.setRepeats(true);
        this.slideInTimer.start();
        this.pauseTimer.start();

        this.setVisible(true);

        setFrameShape();

    }

    private void setFrameShape()
    {
        if (rounded_notification)
        {
            this.setShape(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 20, 20));
        } else
        {
            this.setShape(null);
        }
    }

    @Override
    void reset()
    {
        fadeInTimer.stop();
        fadeOutTimer.stop();
        pauseTimer.stop();
        slideOutTimer.stop();
        slideInTimer.stop();
        waitTimer.stop();
        this.setVisible(false);

        currOpacity = 0;
        setPosition();

        System.gc();
    }

    @Override
    public JPanel getPanel()
    {
        return panel;
    }

}
