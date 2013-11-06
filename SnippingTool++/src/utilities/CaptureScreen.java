package utilities;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import overlay.ScreenBounds;

public class CaptureScreen
{
	private Rectangle screenRectangle;
	private BufferedImage image = null;
	private ScreenBounds bounds = new ScreenBounds();

	public BufferedImage getScreenCapture()
	{
		screenRectangle = bounds.getBounds();

		try
		{
			// Dimension screenSize = new Dimension(screenRectangle.width,
			// screenRectangle.height);

			Robot robot = new Robot();
			image = robot.createScreenCapture(screenRectangle);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return image;
	}
}