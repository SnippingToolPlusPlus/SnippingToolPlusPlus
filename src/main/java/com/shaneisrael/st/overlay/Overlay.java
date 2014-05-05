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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.Preferences;
import com.shaneisrael.st.editor.Editor;
import com.shaneisrael.st.utilities.CaptureScreen;
import com.shaneisrael.st.utilities.Save;
import com.shaneisrael.st.utilities.Upload;

@SuppressWarnings("serial")
public class Overlay extends JPanel implements MouseListener, MouseMotionListener
{
    public static final int SAVE = 0;
    public static final int UPLOAD = 1;

    private int mode = 0;

    private Color selectionColor = new Color(255, 0, 0);
    private Color overlayColor = new Color(0, 0, 0, 100);

    private Rectangle2D selection;
    private Rectangle screenRectangle;
    private Point startPoint = new Point();
    private Point endPoint = new Point();

    private int mouseX, mouseY;
    //used for multi snippet capture
    private ArrayList<Point> startPointList = new ArrayList<Point>();
    private ArrayList<Point> endPointList = new ArrayList<Point>();

    private Font font = new Font("sansserif", Font.BOLD, 12);
    private BufferedImage selectionImage;
    private BufferedImage screenshot;

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
        addMouseMotionListener(this);
        Action submit = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // setOverlayVisible(false); //remove the overlay
                if (Preferences.EDITING_ENABLED)
                {
                    Main.closeCurrentEditor();
                    editor = new Editor(selectionImage, mode); // send the snippet to the editor
                    Main.pointToEditor(editor);
                } else
                {
                    // send the snippet directly to the upload/save queue
                    if (mode == Overlay.UPLOAD)
                    {
                        new Upload(selectionImage, false);
                    } else if (mode == Overlay.SAVE)
                    {
                        save = new Save();
                        save.save(selectionImage);
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

        if (endPoint.x > 0 || endPoint.y > 0)
            drawHelp(g2d);
        try
        {
            Thread.sleep(5);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        this.repaint();

    }

    private void drawHelp(Graphics2D g2d)
    {
        int startX = 0;
        int startY = 0;
        if (endPoint.x > startPoint.x)
        {
            startX = (endPoint.x - 90);
        } else if (endPoint.x < startPoint.x)
        {
            startX = endPoint.x;
        }

        if (endPoint.y > startPoint.y)
        {
            startY = endPoint.y;
        } else if (endPoint.y < startPoint.y)
        {
            startY = endPoint.y - 30;
        }

        g2d.setFont(font);
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(startX, startY, 90, 30);
        g2d.setColor(Color.white);
        g2d.drawRect(startX, startY, 90, 30);
        int tX = startX + 5;
        int tY = startY + font.getSize() + 2;
        int tY2 = startY + (font.getSize() * 2) + 4;
        int tX2 = startX + 11;
        int tX3 = startX + 43;
        g2d.setColor(Color.white);
        g2d.setColor(Color.green);
        g2d.drawString("Press", tX, tY);
        g2d.drawString("to continue", tX2, tY2);
        g2d.setColor(Color.orange);
        g2d.drawString("[enter]", tX3, tY);

    }

    private void drawOverlay(Graphics2D g2d)
    {
        g2d.drawImage(screenshot, 0, 0, null);
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
            g2d.drawImage(selectionImage, select.x, select.y, null);
            g2d.drawRect(select.x, select.y, select.width, select.height);
        }
    }

    private BufferedImage getSubimage()
    {
        Rectangle select = selection.getBounds();
        if (select.width > 0 && select.height > 0)
        {
            return screenshot.getSubimage(select.x, select.y, select.width, select.height);
        } else
        {
            return null;
        }
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        mouseX = me.getX();
        mouseY = me.getY();
        endPoint = new Point(mouseX, mouseY);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
        mouseX = me.getX();
        mouseY = me.getY();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
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
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            startPoint = new Point(mouseX, mouseY);
            endPoint = new Point(mouseX, mouseY);
            startPointList.add(startPoint);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            mouseX = e.getX();
            mouseY = e.getY();
            endPoint = new Point(mouseX, mouseY);
            endPointList.add(endPoint);
        }

    }
}
