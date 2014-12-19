package com.shaneisrael.st.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.shaneisrael.st.SnippingToolPlusPlus;
import com.shaneisrael.st.editor.Editor;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.upload.SimpleFTPUploader;
import com.shaneisrael.st.utilities.CaptureScreen;
import com.shaneisrael.st.utilities.ImageUtilities;
import com.shaneisrael.st.utilities.Save;
import com.shaneisrael.st.utilities.SoundNotifications;
import com.shaneisrael.st.utilities.Upload;

@SuppressWarnings("serial")
public class Overlay extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
    private static final int LEFT_MOUSE_BUTTON = MouseEvent.BUTTON1;
    private static final int MIDDLE_MOUSE_BUTTON = MouseEvent.BUTTON2;
    private static final int RIGHT_MOUSE_BUTTON = MouseEvent.BUTTON3;

    public static final int SAVE = 0;
    public static final int UPLOAD = 1;
    public static final int UPLOAD_FTP = 2;

    private int mode = 0;

    private Color selectionColor = new Color(255, 0, 0);
    private Color overlayColor = new Color(0, 0, 0, 150);
    private Color infoGreenColor = new Color(0, 255, 0, 128);

    private Rectangle2D selection;
    private Rectangle screenRectangle;
    private Point startPoint = new Point();
    private Point endPoint = new Point();

    private int mouseX, mouseY;
    //used for multi snippet capture
    private ArrayList<Point> startPointList = new ArrayList<Point>();
    private ArrayList<Point> endPointList = new ArrayList<Point>();

    private Font infoFont = new Font("sansserif", Font.BOLD, 12);
    private BufferedImage selectionImage;
    private BufferedImage screenshot;
    private BufferedImage background; //this is a slightly larger image than the screenshot used for the Zoom window
    private Image zoomImage; //Since scaling has to be an Image

    private Graphics2D g2d;

    /* Zoom Rects Values */
    private boolean zoomEnabled = true;
    private int zoomDimension = 40;
    private int zoomFactor = 4;
    private int zoomGridMinWidth = zoomFactor * 2;
    private int zoomMargin = 25;
    private int zoomWindowWidth = 120;
    private int zoomCrosshairRadius = 40;
    private int zoomCrosshairMargin = zoomWindowWidth - zoomCrosshairRadius;
    private int zoomX = 0;
    private int zoomY = 0;
    private int screenOffset = zoomWindowWidth;

    private CaptureScreen capture;
    private ScreenBounds bounds;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private OverlayFrame parent;
    private Editor editor;
    private Save save;

    private ArrayList<BufferedImage> selections;
    private ArrayList<Rectangle> selectionRects;
    private Rectangle selectionRect;

    public Overlay(OverlayFrame of)
    {
        setupOverlay();
        parent = of;
        selection = new Rectangle2D.Double();
        addMouseListener(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        Action submit = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //create the multi-snippet image 
                if (selections.isEmpty() == false)
                {
                    selectionImage = ImageUtilities.createMultiSnippet(selections);
                }

                if (Preferences.getInstance().isEditorEnabled())
                {
                    // send the snippet to the editor
                    Editor.getInstance().initialize(selectionImage, mode);
                } else
                {
                    // send the snippet directly to the upload/save queue
                    if (mode == Overlay.UPLOAD)
                    {
                        new Upload(selectionImage, false);
                    }
                    else if (mode == Overlay.SAVE)
                    {
                        save = new Save();
                        save.save(selectionImage);
                    }
                    else if (mode == Overlay.UPLOAD_FTP)
                    {
                        new SimpleFTPUploader(ImageUtilities.saveTemporarily(selectionImage));
                    }

                }
                parent.disposeAll(); // remove the overlay
            }
        };
        Action add = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                SoundNotifications.playShutter();
                selections.add(selectionImage);
                selectionRect = new Rectangle(startPoint);
                selectionRect.add(endPoint);
                selectionRects.add(selectionRect);
            }
        };
        Action escape = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // setOverlayVisible(false); //remove the overlay
                parent.disposeAll();
            }
        };
        this.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        this.getActionMap().put("submit", submit);
        this.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        this.getActionMap().put("escape", escape);
        this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "add");
        this.getActionMap().put("add", add);

        selections = new ArrayList<BufferedImage>();
        selectionRects = new ArrayList<Rectangle>();
        
        setFocusable(true);
        requestFocus();
        setMouseCursor();
    }

    public void setupOverlay()
    {
        startPoint = new Point(0, 0);
        endPoint = new Point(0, 0);
        startPointList.clear();
        endPointList.clear();
        capture = new CaptureScreen();
        bounds = new ScreenBounds();
        screenshot = getScreenShot();

        screenRectangle = bounds.getBounds();
        // setMouseCursor();
    }

    public BufferedImage getScreenShot()
    {
        BufferedImage screenCap = capture.getScreenCapture();
        background = new BufferedImage(screenCap.getWidth() + (screenOffset * 2), screenCap.getHeight()
            + (screenOffset * 2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = background.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fill(new Rectangle(background.getWidth(), background.getHeight()));
        g2d.drawImage(screenCap, screenOffset / 2, screenOffset / 2, null);
        g2d.dispose();

        screenCap = background;

        return screenCap;
    }

    private void setMouseCursor()
    {
        ImageIcon ii = new ImageIcon(this.getClass().getResource("/images/cursor.png"));
        Image cursorImage = ii.getImage();
        Point cursorHotSpot = new Point(0, 0);
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Cursor");
        this.setCursor(customCursor);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(1.35f));

        drawOverlay(g2d);
        drawMultiSnippetShadows(g2d);
        drawSelection(g2d);
        if (selection.getWidth() > 0 && selection.getHeight() > 0)
        {
            drawInfo(g2d);
        }
        try
        {
            Thread.sleep(5);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if (zoomEnabled)
            drawMagnifyingGlass(g2d);

        super.repaint();

    }

    private void drawMultiSnippetShadows(Graphics2D g2d2)
    {
        g2d.setColor(new Color(240,240,240,50));
        for(Rectangle rect : selectionRects)
        {
            g2d.fill(rect);
        }
    }

    private void drawInfo(Graphics2D g2d)
    {
        List<MultiColorText> messages = new ArrayList<>();
        messages.add(getHelpText());
        messages.add(getDimensionText());

        MessageBox helpBox = new MessageBox(messages, infoFont);

        Point infoPoint = calculateInfoPoint(helpBox.getBoundsWithPadding(g2d));
        helpBox.setLocation(infoPoint.x, infoPoint.y);
        helpBox.draw(g2d);
    }

    private MultiColorText getHelpText()
    {
        LinkedHashMap<String, Color> helpMessage = new LinkedHashMap<>();
        helpMessage.put("Press [", infoGreenColor);
        helpMessage.put("enter", Color.orange);
        helpMessage.put("] to continue", infoGreenColor);
        return new MultiColorText(helpMessage);
    }

    private MultiColorText getDimensionText()
    {
        int imageWidth = (int) selection.getWidth();
        int imageHeight = (int) selection.getHeight();
        LinkedHashMap<String, Color> sizeMessage = new LinkedHashMap<>();
        sizeMessage.put("Dimensions: [", infoGreenColor);
        sizeMessage.put("" + imageWidth, Color.orange);
        sizeMessage.put(" x ", infoGreenColor);
        sizeMessage.put("" + imageHeight, Color.orange);
        sizeMessage.put("]", infoGreenColor);
        return new MultiColorText(sizeMessage);
    }

    private Point calculateInfoPoint(Rectangle2D bounds)
    {
        int bWidth = (int) bounds.getWidth();
        int bHeight = (int) bounds.getHeight();

        int infoX = 0;
        int infoY = 0;
        if (endPoint.x > startPoint.x)
        {
            infoX = endPoint.x - bWidth;
        } else if (endPoint.x < startPoint.x)
        {
            infoX = endPoint.x;
        }

        if (endPoint.y > startPoint.y)
        {
            infoY = endPoint.y;
        } else if (endPoint.y < startPoint.y)
        {
            infoY = endPoint.y - bHeight;
        }
        return new Point(infoX, infoY);
    }

    private void drawOverlay(Graphics2D g2d)
    {
        g2d.drawImage(screenshot, -screenOffset / 2, -screenOffset / 2, null);
        g2d.setColor(overlayColor);
        g2d.fillRect(0, 0, screenRectangle.width, screenRectangle.height);
    }

    private void drawSelection(Graphics2D g2d)
    {
        g2d.setColor(selectionColor);

        selection.setFrameFromDiagonal(startPoint, endPoint);
        Rectangle select = selection.getBounds();
        if (getSubimage() != null)
        {
            selectionImage = getSubimage();
            g2d.drawImage(selectionImage, select.x, select.y, null);
            g2d.drawRect(select.x, select.y, select.width, select.height);
        }
    }

    private void drawMagnifyingGlass(Graphics2D g2d)
    {
        Rectangle zoom = getZoomRectangle();

        if (getSubimage(zoom) != null)
        {
            zoomImage = getSubimage(zoom);
            zoomImage = zoomImage.getScaledInstance(zoomWindowWidth, zoomWindowWidth, Image.SCALE_SMOOTH);

            getZoomX();
            getZoomY();

            g2d.drawImage(zoomImage, zoomX, zoomY, null);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRect(zoomX, zoomY, zoomImage.getWidth(null), zoomImage.getHeight(null));

            //draw grid
            int gridWidth = zoomWindowWidth / (zoomWindowWidth / zoomDimension);
            if (gridWidth <= zoomGridMinWidth)
            {
                g2d.setColor(Color.lightGray);
                g2d.setStroke(new BasicStroke(1f));

                for (int i = 0; i <= zoomWindowWidth; i += (Math.round(zoomWindowWidth / zoomDimension)))
                {
                    g2d.drawLine(zoomX + i, zoomY, zoomX + i, zoomY + zoomWindowWidth);
                    g2d.drawLine(zoomX, zoomY + i, zoomX + zoomWindowWidth, zoomY + i);
                }
            }

            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(2.5f));

            g2d.drawLine(zoomX + (zoomWindowWidth / 2), zoomY + zoomCrosshairMargin, zoomX + (zoomWindowWidth / 2),
                zoomY + zoomWindowWidth
                    - zoomCrosshairMargin);
            g2d.drawLine(zoomX + zoomCrosshairMargin, zoomY + (zoomWindowWidth / 2), zoomX + zoomWindowWidth
                - zoomCrosshairMargin, zoomY
                + (zoomWindowWidth / 2));

            g2d.setStroke(new BasicStroke(1.35f));
        }
    }

    private void getZoomX()
    {
        if ((mouseX + zoomMargin + zoomWindowWidth) > screenRectangle.width)
        {
            zoomX = (mouseX - zoomMargin - zoomWindowWidth);
        }
        else if ((mouseX - zoomMargin - zoomWindowWidth) < 0)
        {
            zoomX = (mouseX + zoomMargin);
        }
        else
            zoomX = (mouseX + zoomMargin);
    }

    private void getZoomY()
    {
        if ((mouseY + zoomMargin + zoomWindowWidth) > screenRectangle.height)
        {
            zoomY = (mouseY - zoomMargin - zoomWindowWidth);
        }
        else if ((mouseY - zoomMargin - zoomWindowWidth) < 0)
        {

            zoomY = (mouseY + zoomMargin);
        }
        else
            zoomY = (mouseY + zoomMargin);
    }

    private Rectangle getZoomRectangle()
    {
        return new Rectangle(mouseX - (zoomDimension / 2), mouseY - (zoomDimension / 2), zoomDimension, zoomDimension);
    }

    private BufferedImage getSubimage()
    {
        Rectangle select = selection.getBounds();
        if (select.width > 0 && select.height > 0)
        {
            return screenshot.getSubimage(select.x + (screenOffset / 2), select.y + (screenOffset / 2), select.width,
                select.height);
        } else
        {
            return null;
        }
    }

    private BufferedImage getSubimage(Rectangle rect)
    {
        return screenshot
            .getSubimage(rect.x + (screenOffset / 2), rect.y + (screenOffset / 2), rect.width, rect.height);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (me.getButton() != MIDDLE_MOUSE_BUTTON)
        {
            mouseX = me.getX();
            mouseY = me.getY();
            endPoint = new Point(mouseX, mouseY);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
        mouseX = me.getX();
        mouseY = me.getY();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MIDDLE_MOUSE_BUTTON)
        {
            SoundNotifications.playShutter();
            float op = parent.getOpacity();
            parent.setOpacity(0f);
            screenshot = getScreenShot();
            parent.setOpacity(op);
            parent.repaint();
        }
        else if (e.getButton() == RIGHT_MOUSE_BUTTON)
        {
            zoomEnabled = !zoomEnabled;
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == LEFT_MOUSE_BUTTON)
        {
            startPoint = new Point(mouseX, mouseY);
            endPoint = new Point(mouseX, mouseY);
            startPointList.add(startPoint);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == LEFT_MOUSE_BUTTON)
        {
            mouseX = e.getX();
            mouseY = e.getY();
            endPoint = new Point(mouseX, mouseY);
            endPointList.add(endPoint);
        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int notches = e.getWheelRotation();
        if (notches < 0)
        {
            if (!zoomEnabled)
            {
                if (parent.getOpacity() - .05f >= .005f)
                    parent.setOpacity(parent.getOpacity() - .05f);
            }
            else
            {
                if ((zoomDimension - zoomFactor) >= zoomFactor)
                    zoomDimension -= zoomFactor;
            }
        }
        else
        {
            if (!zoomEnabled)
            {
                if (parent.getOpacity() + .05f <= 1f)
                    parent.setOpacity(parent.getOpacity() + .05f);
                else
                    parent.setOpacity(1f);
            }
            else
            {
                if ((zoomDimension + zoomFactor) <= zoomWindowWidth)
                    zoomDimension += zoomFactor;
            }
        }

    }

    public void dispose()
    {
        selections.clear();
        screenshot = null;
        selectionImage = null;
        background = null;
        zoomImage = null;
        g2d.dispose();
    }
}
