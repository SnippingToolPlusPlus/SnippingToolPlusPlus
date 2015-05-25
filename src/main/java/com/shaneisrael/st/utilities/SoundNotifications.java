package com.shaneisrael.st.utilities;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.shaneisrael.st.data.Logger;

public class SoundNotifications
{
    public static void playDing()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Clip clip = AudioSystem.getClip();
                    URL url = SoundNotifications.class.getResource("/sounds/ding.wav");
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(url);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e)
                {
                    Logger.Log(e);
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
    public static void playShutter()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Clip clip = AudioSystem.getClip();
                    URL url = SoundNotifications.class.getResource("/sounds/shutter.wav");
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(url);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e)
                {
                    Logger.Log(e);
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
    
    
}
