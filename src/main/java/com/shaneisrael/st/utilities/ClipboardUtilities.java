package com.shaneisrael.st.utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.notification.NotificationManager;
import com.shaneisrael.st.notification.STNotificationType;

public abstract class ClipboardUtilities implements ClipboardOwner
{
    /**
     * Sets the system clipboard to a String input
     * 
     * @param str
     */
    public static void setClipboard(String str)
    {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Gets text from the system clipboard
     * 
     * @return
     */
    public static String getClipboardText()
    {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try
        {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e)
        {
            Logger.Log(e);
            e.printStackTrace();
        } catch (IOException e)
        {
            Logger.Log(e);
            e.printStackTrace();
        }
        return null;
    }

    public static void setClipboardImage(final BufferedImage img)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable()
        {
            @Override
            public DataFlavor[] getTransferDataFlavors()
            {
                return new DataFlavor[] { DataFlavor.imageFlavor };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor)
            {
                return DataFlavor.imageFlavor.equals(DataFlavor.imageFlavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
            {
                if (!DataFlavor.imageFlavor.equals(flavor))
                {
                    throw new UnsupportedFlavorException(flavor);
                }
                return img;
            }
        }, null);
        
        NotificationManager.getInstance().showNotification("copied", STNotificationType.SUCCESS);
    }

    /**
     * Uploads any image copies/saved to the clipboard to imgur and then sets the clipboard to the returned url
     */
    public static void uploadImage()
    {
        BufferedImage img = null;
        try
        {
            img = getImageFromClipboard();
        } catch (Exception e1)
        {
            Logger.Log(e1);
            e1.printStackTrace();
        }

        if (img == null)
        {
            NotificationManager.getInstance().showNotification("upload-failed", STNotificationType.ERROR);
        } else
        {
            new Upload(img, false);
        }
    }

    /**
     * Gets the any image currently in the clipboard and returns it as a BufferedImage
     * 
     * @return
     */
    private static BufferedImage getImageFromClipboard()
    {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            try
            {
                return (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
            } catch (Exception e)
            {
                Logger.Log(e);
                e.printStackTrace();
            }
        }

        return null;
    }
}
