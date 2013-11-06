package utilities;

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

public class ImageManipulator
{

	public static Image image;

	static boolean imageLoaded = false;

	public static Image simpleBlur(Image img)
	{

		Image sourceImage = img;

		// Create a buffered image from the source image with a format that's
		// compatible with the screen

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice graphicsDevice = graphicsEnvironment
				.getDefaultScreenDevice();

		GraphicsConfiguration graphicsConfiguration = graphicsDevice
				.getDefaultConfiguration();

		// If the source image has no alpha info use Transparency.OPAQUE instead

		image = graphicsConfiguration.createCompatibleImage(
				sourceImage.getWidth(null), sourceImage.getHeight(null),
				Transparency.BITMASK);

		// Copy image to buffered image

		Graphics graphics = ((BufferedImage) image).createGraphics();

		// Paint the image onto the buffered image

		graphics.drawImage(sourceImage, 0, 0, null);

		graphics.dispose();

		// A 3x3 kernel that blurs an image

		Kernel kernel = new Kernel(3, 3,

		new float[] {

		1f / 9f, 1f / 9f, 1f / 9f,

		1f / 9f, 1f / 9f, 1f / 9f,

		1f / 9f, 1f / 9f, 1f / 9f });

		BufferedImageOp op = new ConvolveOp(kernel);

		for (int i = 0; i < 4; i++)
			// run it through the blur 4 times.
			image = op.filter((BufferedImage) image, null);

		return image;

	}

}
