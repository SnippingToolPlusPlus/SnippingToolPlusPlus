package com.shaneisrael.st.editor;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;

import com.shaneisrael.st.overlay.Overlay;


public class Demo
{
    public static void main(String... args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Editor window = Editor.getInstance();
                    window.initialize(new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB), Overlay.UPLOAD);
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    window.frmEditor.setVisible(true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
