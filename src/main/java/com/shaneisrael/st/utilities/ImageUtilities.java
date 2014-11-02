package com.shaneisrael.st.utilities;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

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

        for (int i = 0; i < 4; i++)
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
        BufferedImage resultImg = null;

        Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName("jpeg");

        //Get the next available writer
        ImageWriter jpegWriter = i.next();

        //Set compression quality
        ImageWriteParam param = jpegWriter.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        File file = new File("temp.jpg");

        try
        {
            FileImageOutputStream output = new FileImageOutputStream(file);

            jpegWriter.setOutput(output);

            IIOImage ioimage = new IIOImage(img, null, null);
            jpegWriter.write(null, ioimage, param);
            output.close();

            resultImg = ImageIO.read(file);

            //delete the temp output file
            file.delete();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
            return resultImg;
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

}
