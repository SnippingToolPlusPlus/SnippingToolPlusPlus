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

import com.shaneisrael.st.Main;
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

    /* Zoom Rects Values */
    private boolean zoomEnabled = false;
    private int zoomDimension = 40;
    private int zoomMargin = 35;
    private int zoomWidth = 200;
    private int zoomCrossMargin = 60;
    private int zoomX = 0;
    private int zoomY = 0;
    private int screenOffset = zoomDimension;


    private CaptureScreen capture;
    private ScreenBounds bounds;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private OverlayFrame parent;
    private Editor editor;
    private Save save;

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
                // setOverlayVisible(false); //remove the overlay
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
                parent.dispose(); // remove the overlay
            }
        };
        Action escape = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // setOverlayVisible(false); //remove the overlay
                parent.dispose();
            }
        };
        this.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        this.getActionMap().put("submit", submit);
        this.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        this.getActionMap().put("escape", escape);

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
        screenshot = capture.getScreenCapture();
        background = new BufferedImage(screenshot.getWidth() + (screenOffset*2), screenshot.getHeight() + (screenOffset*2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = background.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fill(new Rectangle(background.getWidth(), background.getHeight()));
        g2d.drawImage(screenshot, screenOffset/2, screenOffset/2, null);
        g2d.dispose();
        
        screenshot = background;
        
        screenRectangle = bounds.getBounds();
        // setMouseCursor();
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
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawOverlay(g2d);
        drawSelection(g2d);
        
        if(zoomEnabled)
            drawZoomRect(g2d);

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
        this.repaint();
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
        g2d.drawImage(screenshot, -screenOffset/2, -screenOffset/2, null);
        g2d.setColor(overlayColor);
        g2d.fillRect(0, 0, screenRectangle.width, screenRectangle.height);
    }

    private void drawSelection(Graphics2D g2d)
    {
        g2d.setStroke(new BasicStroke(1.35f));
        g2d.setColor(selectionColor);

        selection.setFrameFromDiagonal(startPoint, endPoint);
        Rectangle select = selection.getBounds();
        if (getSubimage() != null)
        {
            selectionImage = getSubimage();
            g2d.drawImage(selectionImage, select.x , select.y, null);
            g2d.drawRect(select.x, select.y, select.width, select.height);
        }
    }

    private void drawZoomRect(Graphics2D g2d)
    {
        Rectangle zoom = getZoomRectangle();
        
        if (getSubimage(zoom) != null)
        {
            zoomImage = getSubimage(zoom);
            zoomImage = zoomImage.getScaledInstance(zoomWidth, zoomWidth, Image.SCALE_SMOOTH);

            getZoomX();
            getZoomY();

            g2d.drawImage(zoomImage, zoomX, zoomY, null);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRect(zoomX, zoomY, zoomImage.getWidth(null), zoomImage.getHeight(null));
            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawLine(zoomX + (zoomWidth / 2), zoomY + zoomCrossMargin, zoomX + (zoomWidth / 2), zoomY + zoomWidth
                - zoomCrossMargin);
            g2d.drawLine(zoomX + zoomCrossMargin, zoomY + (zoomWidth / 2), zoomX + zoomWidth - zoomCrossMargin, zoomY
                + (zoomWidth / 2));
        }
    }

    private void getZoomX()
    {
        if ((mouseX + zoomMargin + zoomWidth) > screenRectangle.width)
        {
            zoomX = (mouseX - zoomMargin - zoomWidth);
        }
        else if ((mouseX - zoomMargin - zoomWidth) < 0)
        {
            zoomX = (mouseX + zoomMargin);
        }
        else
            zoomX = (mouseX + zoomMargin);
    }

    private void getZoomY()
    {
        if ((mouseY + zoomMargin + zoomWidth) > screenRectangle.height)
        {
            zoomY = (mouseY - zoomMargin - zoomWidth);
        }
        else if ((mouseY - zoomMargin - zoomWidth) < 0)
        {

            zoomY = (mouseY + zoomMargin);
        }
        else
            zoomY = (mouseY + zoomMargin);
    }
    private Rectangle getZoomRectangle()
    {
        return  new Rectangle(mouseX - (zoomDimension / 2), mouseY - (zoomDimension / 2), zoomDimension, zoomDimension);
    }

    private BufferedImage getSubimage()
    {
        Rectangle select = selection.getBounds();
        if (select.width > 0 && select.height > 0)
        {
            return screenshot.getSubimage(select.x + (screenOffset/2), select.y + (screenOffset/2), select.width, select.height);
        } else
        {
            return null;
        }
    }

    private BufferedImage getSubimage(Rectangle rect)
    {
        return screenshot.getSubimage(rect.x + (screenOffset/2), rect.y + (screenOffset/2), rect.width, rect.height);
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
            screenshot = capture.getScreenCapture();
            parent.setOpacity(op);
            parent.repaint();
        }
        else if(e.getButton() == RIGHT_MOUSE_BUTTON)
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
            if (parent.getOpacity() - .05f >= .005f)
                parent.setOpacity(parent.getOpacity() - .05f);
        }
        else
        {
            if (parent.getOpacity() + .05f <= 1f)
                parent.setOpacity(parent.getOpacity() + .05f);
            else
                parent.setOpacity(1f);
        }

    }
}
