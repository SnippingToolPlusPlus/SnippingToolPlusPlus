package com.shaneisrael.st.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.utilities.ImageUtilities;

public class EditorPanel extends JPanel implements MouseMotionListener,
    MouseListener
{
    private static final long serialVersionUID = -8925388346037613270L;
    private Image image;
    private BufferedImage editingLayer, clearLayer;
    private Graphics2D editG2D, clearG2D;
    private Color fillColor = Color.red;
    private Color borderColor = Color.black;
    private String tool = "pencil";
    private String text = "";
    private int fontType = Font.PLAIN;
    private int fontSize = 16;

    private Rectangle2D selection;
    private int mx, my, lastX, lastY; // mouse position holders
    private Point clickPoint;
    private float stroke = 3f;

    private Stack<DrawLayer> drawStack;
    private Stack<DrawLayer> redoStack;

    private int dlMinX, dlMinY, dlMaxX, dlMaxY;
    private int drawWidth, drawHeight;

    /*
     * Dashed Stroke
     */
    private float dashed_strokeThickness = 2.0f;
    private float miterLimit = 5f;
    private float[] dashPattern = { 10f };
    private float dashPhase = 5f;
    private BasicStroke dashed_stroke = new BasicStroke(dashed_strokeThickness,
        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, miterLimit,
        dashPattern, dashPhase);
    private Color selectionColor = new Color(36, 193, 255, 50);

    private Editor editor;

    public EditorPanel(BufferedImage img, Editor e)
    {
        editor = e;
        image = img;

        drawWidth = img.getWidth();
        drawHeight = img.getHeight();
        // add 20 to adjust for the stroke width of 10 and the shadows on some
        // shapes.
        editingLayer = new BufferedImage(img.getWidth() + 20,
            img.getHeight() + 20, BufferedImage.TYPE_INT_ARGB);
        editG2D = editingLayer.createGraphics();
        editG2D.setClip(0, 0, drawWidth, drawHeight);
        editG2D.setBackground(new Color(0, 0, 0, 0));
        editG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        editG2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        clearLayer = new BufferedImage(img.getWidth(), img.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        clearG2D = clearLayer.createGraphics();
        clearG2D.setBackground(new Color(0, 0, 0, 0));

        selection = new Rectangle2D.Double(0, 0, drawWidth, drawHeight);

        drawStack = new Stack<DrawLayer>();
        redoStack = new Stack<DrawLayer>();

        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));

    }


    public void setMouseCursor()
    {
        ImageIcon ii = new ImageIcon(this.getClass().getResource(
            "/images/pen.png"));
        Image cursorImage = ii.getImage();
        Point cursorHotSpot = new Point(16, 16);
        Cursor customCursor = this.getToolkit().createCustomCursor(cursorImage,
            cursorHotSpot, "Cursor");
        this.setCursor(customCursor);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(image, 0, 0, null); // Draw the original image first

        Iterator<DrawLayer> iter = drawStack.iterator();
        while (iter.hasNext())
        {
            DrawLayer dl = iter.next();
            g2d.drawImage(dl.getImage(), dl.getLocation().x,
                dl.getLocation().y, null);
        }

        if (tool.equals("text"))
        {
            g2d.drawImage(clearLayer, 0, 0, null);
            g2d.drawImage(editingLayer, 0, 0, null); // Draw the edit layer next
            drawText(editG2D);
        } else
        {
            g2d.drawImage(editingLayer, 0, 0, null); // Draw the edit layer next
            g2d.drawImage(clearLayer, 0, 0, null);
            // drawText(editG2D);
        }

        super.repaint();
        g2d.dispose();
    }

    public void clearEditingLayer()
    {
        editG2D.dispose();
        editingLayer = new BufferedImage(image.getWidth(null) + 20,
            image.getHeight(null) + 20, BufferedImage.TYPE_INT_ARGB);
        editG2D = editingLayer.createGraphics();
        editG2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        editG2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        editG2D.setClip(0, 0, drawWidth, drawHeight);
        editG2D.setBackground(new Color(0, 0, 0, 0));
    }

    public void clearTransparentLayer()
    {
        clearG2D.dispose();
        clearLayer = new BufferedImage(drawWidth, drawHeight,
            BufferedImage.TYPE_INT_ARGB);
        clearG2D = clearLayer.createGraphics();
        clearG2D.setBackground(new Color(0, 0, 0, 0));
    }

    public BufferedImage getImage()
    {
        BufferedImage finalLayer = new BufferedImage(image.getWidth(null),
            image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        editG2D = finalLayer.createGraphics();
        editG2D.drawImage(image, 0, 0, null);
        Iterator<DrawLayer> iter = drawStack.iterator();
        while (iter.hasNext())
        {
            DrawLayer dl = iter.next();
            editG2D.drawImage(dl.getImage(), dl.getLocation().x,
                dl.getLocation().y, null);
        }
        editG2D.dispose();
        return finalLayer;
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
        fillColor = new Color(fillColor.getRed(), fillColor.getGreen(),
            fillColor.getBlue(), level);
    }

    public void setBorderTransparency(int level)
    {
        borderColor = new Color(borderColor.getRed(), borderColor.getGreen(),
            borderColor.getBlue(), level);
    }

    public void setTool(String t)
    {
        if (!t.equals("text") && !t.equals("none"))
        {
            clearTransparentLayer();
            if (editor.inSelectionMode())
                editor.enableTools(true);
            else
                editor.enableTools(false);
        }

        if (this.tool.equals("text") && text.length() > 0)
        {
            submitText();
        }

        this.tool = t;
    }

    public void draw()
    {
        if (!tool.equals("text") && !tool.equals("none"))
            clearG2D.clearRect(0, 0, clearLayer.getWidth(),
                clearLayer.getHeight());

        if (!tool.equals("pencil"))
        {
            editG2D.clearRect(0, 0, editingLayer.getWidth(),
                editingLayer.getHeight());
            editG2D.setBackground(new Color(0, 0, 0, 0));
        } else
            drawPenLine();

        if (tool.equals("line"))
        {
            drawLine(editG2D);
        } else if (tool.equals("rectangle"))
        {
            if (editor.fill())
            {
                if (editor.shadow())
                    drawBorderedRectangle(editG2D);
                else
                    drawFilledRectangle(editG2D);
            } else
                drawRectangle(editG2D);
        } else if (tool.equals("roundRectangle"))
        {
            if (editor.fill())
            {
                if (editor.shadow())
                    drawBorderedRoundRectangle(editG2D);
                else
                    drawFilledRoundRectangle(editG2D);
            }
            else
                drawRoundRectangle(editG2D);
        } else if (tool.equals("ellipse"))
        {
            if (editor.fill())
            {
                if (editor.shadow())
                    drawBorderedEllipse(editG2D);
                else
                    drawFilledEllipse(editG2D);
            }
            else
                drawEllipse(editG2D);
        } else if (tool.equals("select"))
        {
            drawSelectionRegion();
        }
    }

    public void undo()
    {
        if (!drawStack.isEmpty())
        {
            editor.enableRedo();
            redoStack.push(drawStack.pop());
            if (drawStack.isEmpty())
                editor.disableUndo();
        }
    }

    public void redo()
    {
        if (!redoStack.isEmpty())
        {
            editor.enableUndo();
            drawStack.push(redoStack.pop());
            if (redoStack.isEmpty())
                editor.disableRedo();
        }
    }

    private void addNewLayerToStack()
    {
        if (!tool.equals("select"))
        {
            int width = dlMaxX - dlMinX;
            int height = dlMaxY - dlMinY;

            redoStack.clear();
            editor.disableRedo();

            try
            {
                drawStack.add(new DrawLayer(editingLayer
                    .getSubimage((int) (dlMinX - stroke),
                        (int) (dlMinY - stroke), width + 20,
                        height + 20), new Point(
                    (int) (dlMinX - stroke), (int) (dlMinY - stroke))));
            } catch (java.awt.image.RasterFormatException ex)
            {
                Logger.Log(ex);
                System.out.println(dlMinY + height);
            }

            if (!drawStack.isEmpty())
                editor.enableUndo();
            clearEditingLayer();
        }
    }

    private void addSelectionToStack()
    {
        redoStack.clear();
        editor.disableRedo();

        try
        {
            drawStack.add(new DrawLayer(editingLayer
                .getSubimage((int) selection.getX(),
                    (int) selection.getY(), (int) selection.getWidth(),
                    (int) selection.getHeight()), new Point(
                (int) selection.getX(), (int) selection.getY())));
        } catch (java.awt.image.RasterFormatException ex)
        {
            Logger.Log(ex);
        }

        if (!drawStack.isEmpty())
            editor.enableUndo();
        clearEditingLayer();
    }

    public Rectangle draggedRect()
    {

        if (!tool.equals("none") && !tool.equals("text"))
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

    private void setStroke(Graphics2D g)
    {

        if (editor.dashed())
        {
            BasicStroke s = new BasicStroke(stroke,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, miterLimit,
                dashPattern, dashPhase);
            g.setStroke(s);
        }
        else
            g.setStroke(new BasicStroke(stroke));
    }

    private void drawPenLine()
    {
        editG2D.setColor(fillColor);
        setStroke(editG2D);
        editG2D.drawLine(lastX, lastY, mx, my);
    }

    private void drawLine(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        g.drawLine(clickPoint.x, clickPoint.y, mx, my);
    }

    private void drawEllipse(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        Rectangle rect = draggedRect();
        g.drawOval(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawFilledEllipse(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        Rectangle rect = draggedRect();
        g.fillOval(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        g.draw(draggedRect());
    }

    private void drawRoundRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        Rectangle rect = draggedRect();
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height,
            rect.height / 2, rect.height / 2);
    }

    private void drawFilledRoundRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        Rectangle rect = draggedRect();
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height,
            rect.height / 2, rect.height / 2);
    }

    private void drawFilledRectangle(Graphics2D g)
    {
        g.setColor(fillColor);
        setStroke(g);
        g.fill(draggedRect());
    }

    private void drawBorderedRectangle(Graphics2D g)
    {
        Rectangle rect = draggedRect();
        setStroke(g);
        Color shadow = new Color(borderColor.getRed(), borderColor.getGreen(),
            borderColor.getBlue(), 100);
        // Color last = g.getColor();
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
            } else
            {
                stroke_correction--;
            }
        }
        g.fillRect((int) (rect.getX() + rect.getWidth())
            + stroke_correction / 2, (int) rect.getY()
            + stroke_correction, stroke_correction, (int) rect
            .getHeight() - stroke_correction / 2);
        g.fillRect((int) (rect.getX() + stroke_correction),
            (int) (rect.getY() + rect.getHeight())
                + stroke_correction / 2, (int) rect
                .getWidth() + stroke_correction / 2,
            stroke_correction);
        /*
         * End Shadow
         */

        g.setColor(fillColor);
        g.fill(new Rectangle(rect.x + stroke_correction / 2,
            rect.y + stroke_correction / 2,
            rect.width - stroke_correction / 2,
            rect.height - stroke_correction / 2));

        g.setColor(borderColor);
        g.draw(rect);

        g.setColor(fillColor);
    }

    private void drawBorderedRoundRectangle(Graphics2D g)
    {
        Rectangle rect = draggedRect();
        setStroke(g);

        Color shadow = new Color(borderColor.getRed(), borderColor.getGreen(),
            borderColor.getBlue(), 100);
        // Color last = g.getColor();
        g.setColor(shadow);

        g.setColor(fillColor);
        g.fillRoundRect(rect.x,
            rect.y,
            rect.width,
            rect.height, (int) rect.getWidth() / 2, (int) rect.getHeight() / 2);

        g.setColor(borderColor);
        g.drawRoundRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight(),
            (int) rect.getWidth() / 2, (int) rect.getHeight() / 2);

        g.setColor(fillColor);
    }

    private void drawBorderedEllipse(Graphics2D g)
    {
        Rectangle rect = draggedRect();
        setStroke(g);
        Color shadow = new Color(borderColor.getRed(), borderColor.getGreen(),
            borderColor.getBlue(), 100);
        // Color last = g.getColor();
        g.setColor(shadow);

        g.setColor(fillColor);
        g.fillOval(rect.x,
            rect.y,
            rect.width,
            rect.height);

        g.setColor(borderColor);
        g.drawOval(rect.x, rect.y, rect.width, rect.height);

        g.setColor(fillColor);
    }

    private void drawSelectionRegion()
    {
        Graphics2D g = clearG2D;
        g.setColor(selectionColor);
        g.fill(draggedRect());
        g.setColor(Color.white);
        g.setStroke(new BasicStroke(dashed_strokeThickness));
        g.draw(draggedRect());
        g.setColor(Color.black);
        g.setStroke(dashed_stroke);
        g.draw(draggedRect());

    }

    public void drawText(Graphics2D g)
    {
        Font f = new Font("Georgia", fontType, fontSize);
        g.setFont(f);
        FontMetrics fm1 = g.getFontMetrics();
        g.setFont(f);
        g.setColor(fillColor);
        g.setClip(selection);
        String lines[] = text.split("\n");
        g.clearRect((int) selection.getX(), (int) selection.getY(),
            (int) selection.getWidth(), (int) selection.getHeight());

        if (lines.length - 1 >= 0)
        {
            if (fm1.stringWidth(lines[lines.length - 1]) > selection.getWidth()
                - fontSize / 2)
                if (lines.length * g.getFontMetrics().getHeight() > selection
                    .getHeight() - g.getFontMetrics().getHeight())
                {
                    selection.setFrame(new Rectangle((int) selection.getX(), (int) selection.getY(), (int) selection
                        .getWidth(), (int) selection.getHeight() + g.getFontMetrics().getHeight()));
                    clearTransparentLayer();
                    drawSelectionRegion();

                } else
                    lineWrapText();
        }

        int y = (int) (selection.getY());
        for (String line : text.split("\n"))
            g.drawString(line, (int) selection.getX(), y += g.getFontMetrics()
                .getHeight());
    }

    private void lineWrapText()
    {
        String split[] = text.split(" ");
        text = "";
        for (int i = 0; i < split.length - 1; i++)
            text += split[i] + " ";
        addWriteText('\n');
        text += split[split.length - 1];
    }

    public void blurSelection()
    {
        if (tool.equals("select"))
        {
            int width = (int) selection.getWidth();
            int height = (int) selection.getHeight();
            int x = (int) selection.getX();
            int y = (int) selection.getY();
            if (y + height > drawHeight)
                height = drawHeight - y;
            if (x + width > drawWidth)
                width = drawWidth - x;
            if (x > drawWidth)
                x = drawWidth;
            if (y > drawHeight)
                y = drawHeight;

            editG2D.drawImage(image, 0, 0, null);
            Iterator<DrawLayer> iter = drawStack.iterator();
            while (iter.hasNext())
            {
                DrawLayer dl = iter.next();
                editG2D.drawImage(dl.getImage(), dl.getLocation().x,
                    dl.getLocation().y, null);
            }

            Image blurImg = ImageUtilities.simpleBlur(editingLayer.getSubimage(
                x, y, width, height));
            drawStack.add(new DrawLayer((BufferedImage) blurImg,
                new Point(x, y)));
            clearEditingLayer();
        }
    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (me.getButton() != MouseEvent.BUTTON3)
        {
            lastX = mx;
            lastY = my;
            mx = me.getX();
            my = me.getY();

            if (editor.inSelectionMode() && tool.equals("text"))
            {
                selection
                    .setFrame(new Rectangle((int) selection.getX()
                        - (lastX - mx), (int) selection.getY()
                        - (lastY - my), (int) selection.getWidth(),
                        (int) selection.getHeight()));
                clearTransparentLayer();
                clearEditingLayer();
                drawSelectionRegion();
                drawText(editG2D);
            }

            if (mx < 0)
                dlMinX = 0;
            else if (mx < dlMinX)
                dlMinX = mx;
            if (mx > drawWidth)
                dlMaxX = drawWidth;
            else if (mx > dlMaxX)
                dlMaxX = mx;
            if (my < 0)
                dlMinY = 0;
            else if (my < dlMinY)
                dlMinY = my;
            if (my > drawHeight)
                dlMaxY = drawHeight;
            else if (my > dlMaxY)
                dlMaxY = my;

            draw(); // get selected tool and draw
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
        if (me.getButton() != MouseEvent.BUTTON3)
        {
            clickPoint = new Point(mx, my);
            dlMinX = mx;
            dlMaxX = mx;
            dlMinY = my;
            dlMaxY = my;
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        if (!editor.inSelectionMode())
            addNewLayerToStack();
    }

    public void addWriteText(char character)
    {
        this.text += character;
        //If new lines are larger than the selection region, we increase the height of the selection
        if (character == '\n')
        {
            int lines = 0;
            for (int i = 0; i < text.length(); i++)
                if (i + 1 < text.length())
                {
                    String temp = text.substring(i, i + 1);
                    if (temp.equals("\n"))
                        lines++;
                }
            if (lines * editG2D.getFontMetrics().getHeight() > selection
                .getHeight() - editG2D.getFontMetrics().getHeight())
            {
                selection.setFrame(new Rectangle((int) selection.getX(), (int) selection.getY(), (int) selection
                    .getWidth(), (int) selection.getHeight() + editG2D.getFontMetrics().getHeight()));
                clearTransparentLayer();
                drawSelectionRegion();
            }
        }
    }

    public void backspaceWriteText()
    {
        if (text.length() > 0)
        {
            if (text.length() >= 2
                && text.substring(text.length() - 2, text.length()).equals(
                    "\n"))
                text = text.substring(0, text.length() - 2);
            else
                text = text.substring(0, text.length() - 1);
        }
    }

    public void submitText()
    {
        addSelectionToStack();
        text = "";
        setTool("select");
        clearTransparentLayer();
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
    }

    public void disposeAll()
    {
    }

    public void reset()
    {
        drawStack.clear();
        redoStack.clear();
        editor.disableRedo();
        editor.disableUndo();
        clearEditingLayer();
    }

    public void dispose()
    {
        drawStack.clear();
        redoStack.clear();
        editG2D.dispose();
        clearG2D.dispose();
        editingLayer = null;
        clearLayer = null;
        image = null;
    }

    public void setFontType(int plain)
    {
        this.fontType = plain;
    }

    public void setFontSize(int size)
    {
        this.fontSize = size;

    }
}
