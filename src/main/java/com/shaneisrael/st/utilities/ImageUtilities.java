package com.shaneisrael.st.utilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtilities
{
    public static Image image;

    static boolean imageLoaded = false;

    public static Image simpleBlur(Image img)
    {
        Image sourceImage = img;

        // Create a buffered image from the source image with a format that's compatible with the screen
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

        // If the source image has no alpha info use Transparency.OPAQUE instead
        image = graphicsConfiguration.createCompatibleImage(sourceImage.getWidth(null), sourceImage.getHeight(null),
            Transparency.BITMASK);

        // Copy image to buffered image
        Graphics graphics = ((BufferedImage) image).createGraphics();

        // Paint the image onto the buffered image
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.dispose();

        // A 3x3 kernel that blurs an image
        Kernel kernel = new Kernel(3, 3, new float[] {
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f });

        BufferedImageOp op = new ConvolveOp(kernel);

        for (int i = 0; i < 2; i++)
        {
            image = op.filter((BufferedImage) image, null);
        }
        return image;
    }

    /**
     * Compresses a BufferedImage down to the specified quality
     * 
     * @param img
     *            the BufferedImage to be compressed
     * @param quality
     *            The desired resulting image quality between 0.0 and 1f
     * 
     * @return returns a new compressed BufferedImage
     */
    public static BufferedImage compressImage(BufferedImage img, float quality)
    {
        File input = saveTemporarily(img);
        
        try
        {
            BufferedImage image = ImageIO.read(input);
    
            File compressedImageFile = new File("compress.jpg");
            OutputStream os =new FileOutputStream(compressedImageFile);
    
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = (ImageWriter) writers.next();
    
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);
    
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), param);
            os.close();
            ios.close();
            writer.dispose();
            
            image = ImageIO.read(compressedImageFile);
            
            compressedImageFile.delete();
            
            return image;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 
     * @param image
     *          the BufferedImage create a temp file from
     * @return
     *          returns a file path to a temporary file
     */
    public static File saveTemporarily(BufferedImage image)
    {
        try
        {
            File file = File.createTempFile("stpp-", "-snip.png");
            ImageIO.write(image, "png", file);

            return file;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param selections
     *          An array list of BufferedImages
     * @return 
     *          returns a combined image
     */
    public static BufferedImage createMultiSnippet(ArrayList<BufferedImage> selections)
    {
        Graphics2D g2d;
        BufferedImage result = null;
        int maxWidth = 0;
        int maxHeight = 0;
        
        for(BufferedImage img : selections)
        {
            if(img.getWidth() > maxWidth)
                maxWidth = img.getWidth();
            maxHeight += img.getHeight();
        }
        
        result = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        g2d = result.createGraphics();
        g2d.setColor(Color.white);
        g2d.fill(new Rectangle(maxWidth, maxHeight));
        
        int y = 0;
        for(BufferedImage img : selections)
        {
            g2d.drawImage(img, 0, y, null);
            y+=img.getHeight();
        }
        g2d.dispose();
        
        return result;
    }

}
