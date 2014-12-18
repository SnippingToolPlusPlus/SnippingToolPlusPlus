package com.shaneisrael.st.notification;

import com.shaneisrael.st.Config;
import com.shaneisrael.st.prefs.Preferences;

public class NotificationManager
{
    private static NotificationManager manager;
    private static STNotificationQueue notificationQueue;
    private static STNotification notification;

    public NotificationManager()
    {
        initializeNotifications();
    }
    private void initializeNotifications()
    {
        STTheme.setThemePath(Config.THEME_PATH);
        notificationQueue = new STNotificationQueue(17);
    }
    
    public void showNotification(STNotification notification)
    {
        notificationQueue.add(notification);
    }
    
    public void showNotification(String title, STNotificationType type)
    {
        notification = new STNotification(title, type);
        notification.setPauseTime(1500);
        notificationQueue.add(notification);
    }

    public void showNotification(String title, STNotificationType type, int pauseTime)
    {
        notification = new STNotification(title, type);
        notification.setPauseTime(pauseTime);
        notificationQueue.add(notification);
    }
    
    public static NotificationManager getInstance()
    {
        if(manager == null)
        {
            manager = new NotificationManager();
            return manager;
        }
        return manager;
    }
}
