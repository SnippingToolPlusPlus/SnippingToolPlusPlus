package com.shaneisrael.st.utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.shaneisrael.st.Main;

public abstract class ClipboardUtilities implements ClipboardOwner
{
    private static Upload upload;

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
            e.printStackTrace();
        } catch (IOException e)
        {
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
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (img == null)
        {

            Main.displayErrorMessage("Upload Error", "An image could not be found in the clipboard.");
        } else
            upload = new Upload(img, false);
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
            } // catch (UnsupportedFlavorException | IOException e) //Java 1.7
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void sendTextToPastebin()
    {
        new PastebinUploader(getClipboardText());
    }

    private class TransferableImage implements Transferable
    {

        BufferedImage i;

        public TransferableImage(BufferedImage i)
        {
            this.i = i;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
        {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null)
            {
                return i;
            } else
            {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++)
            {
                if (flavor.equals(flavors[i]))
                {
                    return true;
                }
            }

            return false;
        }
    }

}
