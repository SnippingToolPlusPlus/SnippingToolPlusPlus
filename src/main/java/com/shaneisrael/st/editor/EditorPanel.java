package com.shaneisrael.st.editor;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.utilities.ClipboardUtilities;
import com.shaneisrael.st.utilities.ImageUtilities;

public class EditorPanel extends JPanel implements MouseMotionListener, MouseListener
{
    private static final long serialVersionUID = -8925388346037613270L;
    private Image image;
    private BufferedImage editingLayer;
    //laygerG2D is the transparent layer for rectangles and other shapes not lines
    private Graphics2D editG2D, layerG2D, textLayerG2D;
    //this is for adding the final edits to the main image before upload
    private Graphics2D addG2D;
    private Color fillColor = Color.red;
    private Color borderColor = Color.black;
    private String tool = "pencil";

    private Rectangle2D selection;
    private int mx, my, lastX, lastY; // mouse position holders
    private Point clickPoint;
    private Point textPoint = new Point(0, 0);
    private Dimension editSize;
    private float stroke = 3f;
    private Rectangle blurredSelection;

    private Font textFont = new Font("verdana", Font.BOLD, 14);
    private String drawText = "";

    /*
     * Dashed Stroke
     */
    private float dashed_strokeThickness = 2.0f;

    private float miterLimit = 5f;
    private float[] dashPattern = { 5f };
    private float dashPhase = 5f;
    private BasicStroke dashed_stroke = new BasicStroke(dashed_strokeThickness, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase);

    /*
     * 
     * TODO: Update how drawing text works. First, draw out a box, this box
     * will be the bounds of where you can draw text. use this code int chars =
     * g.getFontMetrics().stringWidth(text); to get the string length in pixels
     * so that you know when your at the edge of the box and have to create a
     * new line.
     */

    /*
     * TODO: When submitting, empty null text is leaving a red rectangle on the
     * image usually at the upper left area.
     */

    public EditorPanel(BufferedImage img, Editor e)
    {
        image = img;

        addG2D = ((BufferedImage) image).createGraphics();
        addG2D.setStroke(new BasicStroke(stroke));
        addG2D.setColor(fillColor);
        editSize = new Dimension(img.getWidth(), img.getHeight());
        editG2D = ((BufferedImage) image).createGraphics();
        editingLayer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        layerG2D = editingLayer.createGraphics();
        layerG2D.setBackground(new Color(0, 0, 0, 0)); // set layer transparent
        layerG2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.0f));
        layerG2D = editingLayer.createGraphics();
        layerG2D.setColor(fillColor);

        textLayerG2D = editingLayer.createGraphics();
        textLayerG2D.setBackground(new Color(0, 0, 0, 0)); // set layer transparent
        textLayerG2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.0f));
        textLayerG2D = editingLayer.createGraphics();
        textLayerG2D.setColor(fillColor);

        selection = new Rectangle2D.Double();

        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));

        if(OperatingSystem.isWindows()) {
            setMouseCursor();
        }
    }

    public void setMouseCursor()
    {
        ImageIcon ii = new ImageIcon(this.getClass().getResource("/images/pen.png"));
        Image cursorImage = ii.getImage();
        Point cursorHotSpot = new Point(16, 16);
        Cursor customCursor = this.getToolkit().createCustomCursor(cursorImage, cursorHotSpot, "Cursor");
        this.setCursor(customCursor);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(image, 0, 0, null); // Draw the snippet image first
        drawEditingLayer(g2d); // draw the transparent image on top

        if (tool.equals("text") && textPoint != null)
            draw();
        this.repaint();
    }

    private void drawEditingLayer(Graphics2D g2d)
    {
        /*
         * This is the transparent layer that all the editing
         * "squares, lines, drawing, etc" is drawn on. It sits on top of the
         * actual snippet image
         */
        g2d.drawImage(editingLayer, 0, 0, null);
    }

    public BufferedImage getImage()
    {
        return (BufferedImage) image;
    }

    public void setColor(Color color)
    {
        fillColor = color;
    }

    public void setBorderColor(Color color)
    {
        borderColor = color;
    }

    public void setTransparency(int level)
    {
        fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), level);
    }

    public void setBorderTransparency(int level)
    {
        borderColor = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), level);
    }

    public void setTool(String tool)
    {
        // If there is text on the screen, and the tool is changed. We want that text added to the final image.
        if (!drawText.equals(""))
        {
            forceDrawText();
        }
        this.tool = tool;
    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        lastX = mx;
        lastY = my;
        mx = me.getX();
        my = me.getY();

        draw(); // get selected tool and draw

        this.repaint();
    }

    private void draw()
    {
        if (tool.equals("pencil"))
        {
            drawPenLine(mx, my, lastX, lastY);
        } else if (tool.equals("line"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            drawLine(layerG2D);
        } else if (tool.equals("rectangle"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            drawRectangle(layerG2D);
        } else if (tool.equals("filled rectangle"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            drawFilledRectangle(layerG2D);
        } else if (tool.equals("bordered rectangle"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            drawBorderedRectangle(layerG2D);
        } else if (tool.equals("text"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            drawText(layerG2D);
        } else if (tool.equals("blur"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
            layerG2D.setBackground(new Color(0, 0, 0, 0));
            createBlurSelection(layerG2D);
            createBlur(layerG2D);
        }

    }

    public Rectangle draggedRect()
    {
        if (mx < 0)
        {
            selection.setFrameFromDiagonal(clickPoint, new Point(0, my));
        } else if (my < 0)
        {
            selection.setFrameFromDiagonal(clickPoint, new Point(mx, 0));
        } else if (my < 0 && mx < 0)
        {
            selection.setFrameFromDiagonal(clickPoint, new Point(0, 0));
        } else
        {
            selection.setFrameFromDiagonal(clickPoint, new Point(mx, my));
        }
        return selection.getBounds();
    }

    public void forceDrawText()
    {
        drawText(addG2D);
        drawText = "";
    }

    private void drawText(Graphics2D g)
    {
        // draws to layerG2D
        g.setColor(fillColor);
        g.setFont(textFont);
        drawString(g, drawText, textPoint.x, textPoint.y);
    }

    private void drawString(Graphics g, String text, int x, int y)
    {
        for (String line : text.split("\n"))
        {
            g.drawString(line, x, y += 8);
        }
    }

    private void drawPenLine(int mx2, int my2, int lastX2, int lastY2)
    {
        editG2D.setColor(fillColor);
        editG2D.setStroke(new BasicStroke(stroke));
        editG2D.drawLine(mx2, my2, lastX2, lastY2);
    }

    private void drawLine(Graphics2D g)
    {
        g.setColor(fillColor);
        g.setStroke(new BasicStroke(stroke));
        g.drawLine(clickPoint.x, clickPoint.y, mx, my);
    }

    private void drawRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        g.setStroke(new BasicStroke(stroke));
        g.draw(draggedRect());
    }

    private void drawFilledRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        g.setStroke(new BasicStroke(stroke));
        g.fill(draggedRect());
    }

    private void createBlurSelection(Graphics2D g)
    {
        // this variable is set so that later in the addEditToImage method, it can retrieve the subimage at that location and blur it then add it.
        blurredSelection = draggedRect();
        g.setStroke(dashed_stroke);
        g.setColor(new Color(0, 0, 0, 200));
        g.draw(blurredSelection);
    }

    private void createBlur(Graphics2D g)
    {

        blurredSelection = draggedRect();
        Image blur = getSubImage(blurredSelection);
        blur = ImageUtilities.simpleBlur(blur);
        g.drawImage(blur, blurredSelection.x, blurredSelection.y, null);
    }

    private BufferedImage getSubImage(Rectangle selection)
    {
        BufferedImage img = (BufferedImage) image;
        int height = selection.height;
        int width = selection.width;
        int x = selection.x;
        int y = selection.y;

        if ((selection.y + selection.height) > img.getHeight())
        {
            height = img.getHeight() - selection.y;
        }
        if ((selection.x + selection.width) > img.getWidth())
        {
            width = img.getWidth() - selection.x;
        }
        if (y < 0)
        {
            height += y;
            y = 0;
        }
        if (x < 0)
        {
            width += x;
            x = 0;
        }
        return img.getSubimage(x, y, width, height);
    }

    private void drawBorderedRectangle(Graphics2D g)
    {
        g.setStroke(new BasicStroke(stroke));

        Color shadow = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 100);
        //Color last = g.getColor();
        g.setColor(shadow);

        /*
         * Right bordered shadow
         */

        int stroke_correction = Math.round(stroke);
        if (stroke_correction % 2 != 0)
        {
            if (stroke_correction % 2 >= .5)
            {
                stroke_correction++;
            }
            else
            {
                stroke_correction--;
            }
        }

        g.fillRect((int) (draggedRect().getX() + draggedRect().getWidth()) + (int) (stroke_correction / 2),
                (int) draggedRect().getY() + stroke_correction, stroke_correction, (int) draggedRect().getHeight()
                        - (int) (stroke_correction / 2));
        g.fillRect((int) (draggedRect().getX() + stroke_correction), (int) (draggedRect().getY() + draggedRect()
                .getHeight()) + (int) (stroke_correction / 2), (int) draggedRect().getWidth()
                + (int) (stroke_correction / 2), stroke_correction);
        /*
         * End Shadow
         */

        g.setColor(fillColor);
        g.fill(new Rectangle(draggedRect().x + (int) (stroke_correction / 2), draggedRect().y
                + (int) (stroke_correction / 2), draggedRect().width - (int) (stroke_correction / 2),
                draggedRect().height - (int) (stroke_correction / 2)));

        g.setColor(borderColor);
        g.draw(draggedRect());

        g.setColor(fillColor);
    }

    public void addEditToImage()
    {
        addG2D.setColor(fillColor);
        // Skips pencil tool because the pencil is added directly

        if (tool.equals("rectangle"))
        {
            drawRectangle(addG2D);
        } else if (tool.equals("line"))
        {
            drawLine(addG2D);
        } else if (tool.equals("filled rectangle"))
        {
            drawFilledRectangle(addG2D);
        } else if (tool.equals("bordered rectangle"))
        {
            drawBorderedRectangle(addG2D);
        } else if (tool.equals("blur"))
        {
            createBlur(addG2D); // create the blur from the selection
        } else if (tool.equals("text"))
        {
            if (!(drawText.isEmpty()))
            {
                drawText = drawText.trim();
                drawText(addG2D);
            }
            drawText = "";
        }
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
        mx = me.getX();
        my = me.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            textPoint = new Point(e.getPoint().x, e.getPoint().y);
            if (drawText.length() > 0)
                addEditToImage();
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
    public void mousePressed(MouseEvent me)
    {
        clickPoint = new Point(mx, my);
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        if (!tool.equals("pencil") && !tool.equals("text"))
        {
            layerG2D.clearRect(0, 0, editSize.width, editSize.height);
        }
        addEditToImage();
    }

    public void setDrawText(String text)
    {
        drawText = text;
    }

    public String getText()
    {
        return drawText;
    }

    public void backspaceDrawText()
    {
        String currentChar, nextChar, enter;
        System.out.println(drawText);
        if (drawText.length() > 0)
        {
            if (drawText.length() > 1)
            {
                currentChar = drawText.substring(drawText.length() - 1, drawText.length());
                nextChar = drawText.substring(drawText.length() - 2, drawText.length() - 1);
                enter = nextChar + currentChar;
                if (enter.equals("\n"))
                    drawText = drawText.substring(0, drawText.length() - 2);
                else
                    drawText = drawText.substring(0, drawText.length() - 1);

                System.out.println(drawText.substring(0, drawText.length() - 1));

            } else
                drawText = drawText.substring(0, drawText.length() - 1);
        }
    }

    public String getTool()
    {
        return tool;
    }

    public void setStroke(float stroke)
    {
        this.stroke = stroke;

    }

    public void copyImageToClipboard()
    {
        ClipboardUtilities.setClipboardImage(getImage());
    }
}
