package com.shaneisrael.st.notification;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author Shane
 * 
 *         Will start the animation process of the first notification in the queue then remove it and proceed to the next notification in queue. This insures
 *         that all notifications are displayed fairly and user has time to see each one before they leave.
 */
public class STNotificationQueue implements Runnable
{
    private Queue<STNotification> queue;
    private int pauseTime = 1000;
    private int rate;

    private boolean running = false;

    private Thread thread;

    public STNotificationQueue(int rate)
    {
        queue = new LinkedList<STNotification>();
        this.rate = rate;
        if (this.rate > 20)
            rate = 20;

    }
    public int getSize()
    {
    	return queue.size();
    }

    public void add(STNotification n)
    {
        queue.add(n);

        if (!running)
        {
        	running = true;
        	thread = new Thread(this);
        	thread.start();
        }
    }

    public void run()
    {
        while (!queue.isEmpty())
        {
            STNotification next = queue.poll();
            
            next.setAutoRequestFocus(false);
            next.setAlwaysOnTop(true);
            next.setVisible(true);
            this.pauseTime = next.getPauseTime();
            int start = next.getLocation().y;
            int travelLocation = STTheme.getTravelLocation();
            
            try
            {
                while (next.getLocation().y > (travelLocation))
                {
                    next.setLocation(next.getX(), next.getLocation().y - 1);
                    Thread.sleep(20 - rate);
                }
                Thread.sleep(pauseTime);
                while (next.getLocation().y < start)
                {
                    next.setLocation(next.getX(), next.getLocation().y + 1);
                    Thread.sleep(20 - rate);
                }
                next.dispose();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        running = false;
    }
}
