package com.shaneisrael.st.editor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.overlay.Overlay;
import com.shaneisrael.st.upload.SimpleFTPUploader;
import com.shaneisrael.st.utilities.ImageUtilities;
import com.shaneisrael.st.utilities.Save;
import com.shaneisrael.st.utilities.Upload;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.JCheckBox;

public class Editor
{

    private JPanel editorPanel;
    private JButton btnUndo, btnRedo, btnReset, blurToolBtn, textToolBtn, toggleButton_8, toggleButton_9;

    private JToggleButton btnSelect;
    private JCheckBox chckbxShadow, chckbxFilled;
    JFrame frmEditor;
    private Color fillColor;
    private Color borderColor;
    private JColorChooser colorChooser;
    private JSlider opacitySlider;
    private JSlider strokeSlider;
    private JLayeredPane layeredPane;

    private ButtonGroup toolGroup;
    private BufferedImage image;
    private int mode;
    private Save save;
    private Upload upload;
    
    private KeyEventDispatcher keyDispatcher;
    private static Editor instance = null;

    /**
     * Create the application.
     */
    public Editor()
    {
        


    }
    private BufferedImage getEditedImage()
    {
        return ((EditorPanel)editorPanel).getImage();
    }

    public void submit()
    {

        if (mode == Overlay.SAVE)
        {
            save = new Save();
            save.save(getEditedImage());
        } else if (mode == Overlay.UPLOAD)
        {
            upload = new Upload(getEditedImage(), false);
        } else if (mode == Overlay.UPLOAD_FTP)
        {
            new SimpleFTPUploader(
                ImageUtilities.saveTemporarily(getEditedImage()));
        }

        exit();
    }
    public void exit()
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .removeKeyEventDispatcher(keyDispatcher);
        keyDispatcher = null;
        ((EditorPanel) editorPanel).dispose();
        instance.dispose();
    }
    public static Editor getInstance()
    {
        if (instance == null)
        {
            instance = new Editor();
        } else
        {
            //instance.dispose();
            instance = new Editor();
        }
        return instance;

    }
    public void dispose()
    {
        frmEditor.dispose();
    }
    /**
     * Initialize the contents of the frame.
     */
    /**
     * @wbp.parser.entryPoint
     */

    public void initialize(BufferedImage img, int m)
    {
        this.image = img;
        this.mode = m;
        
        frmEditor = new JFrame();
        frmEditor.setType(Type.UTILITY);
        frmEditor.setTitle("Editor");
        frmEditor.setBounds(100, 100, 1280, 720);
        frmEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmEditor.setIconImage(Toolkit.getDefaultToolkit().getImage(
            Editor.class.getResource("/images/icons/utilities.png")));
        frmEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        keyDispatcher = new KeyEventDispatcher()
        {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                if (e.isControlDown())
                {
                    if(e.getID() == KeyEvent.KEY_PRESSED)
                    {
                        switch (e.getKeyCode()) {
                        case KeyEvent.VK_Z:
                            ((EditorPanel) editorPanel).undo();
                            break;
                        case KeyEvent.VK_Y:
                            ((EditorPanel) editorPanel).redo();
                            break;
                        }
                    }
                }
                else if (e.isShiftDown())
                {
                    if(e.getID() == KeyEvent.KEY_TYPED)
                    {
                        if(((EditorPanel) editorPanel).getTool().equals("text") && inSelectionMode())
                        {
                            if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                                ((EditorPanel) editorPanel).backspaceWriteText();
                            else if(e.getKeyChar() == KeyEvent.VK_SPACE)
                                ((EditorPanel) editorPanel).addWriteText(' ');
                            else if(e.getKeyChar() == KeyEvent.VK_ENTER)
                                ((EditorPanel) editorPanel).submitText();
                            else
                                ((EditorPanel) editorPanel).addWriteText(((char)e.getKeyChar()));

                        }
                    }
                    if(e.getID() == KeyEvent.KEY_RELEASED)
                    {
                        switch(e.getKeyCode())
                        {
                        case KeyEvent.VK_ENTER:
                            submit();
                            break;
                            
                        }
                    }
                }
                else
                {
                    if(e.getID() == KeyEvent.KEY_TYPED)
                    {
                        if(((EditorPanel) editorPanel).getTool().equals("text") && inSelectionMode())
                        {
                            if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                                ((EditorPanel) editorPanel).backspaceWriteText();
                            else if(e.getKeyChar() == KeyEvent.VK_SPACE)
                                ((EditorPanel) editorPanel).addWriteText(' ');
                            else if(e.getKeyChar() == KeyEvent.VK_ENTER)
                                ((EditorPanel) editorPanel).submitText();
                            else
                                ((EditorPanel) editorPanel).addWriteText(((char)e.getKeyChar()));

                        }
                    }
                    if(e.getID() == KeyEvent.KEY_RELEASED)
                    {
                        switch(e.getKeyCode())
                        {
                        case KeyEvent.VK_ESCAPE:
                            exit();
                            break;
                        }
                    }
                }
                return false;
            }

        };

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(keyDispatcher);
        
        toolGroup = new ButtonGroup();

        JPanel panel = new JPanel();
        frmEditor.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new MigLayout("", "[823.00,grow]", "[82.00][425.00,grow]"));

        JPanel toolPanel = new JPanel();
        toolPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        toolPanel.setBackground(Color.WHITE);
        toolPanel.setForeground(Color.BLACK);
        panel.add(toolPanel, "cell 0 0,grow");
        toolPanel.setLayout(new MigLayout("", "[110.00,leading][][][][51.00][120.00][][][][][52.00][][][][][][]",
            "[66.00,grow]"));

        opacitySlider = new JSlider();
        opacitySlider.setBackground(Color.WHITE);
        opacitySlider.setMinorTickSpacing(5);

        toolPanel.add(opacitySlider, "cell 1 0");

        strokeSlider = new JSlider();
        strokeSlider.setPaintTicks(true);
        strokeSlider.setPaintLabels(true);
        strokeSlider.setBackground(Color.WHITE);
        toolPanel.add(strokeSlider, "cell 2 0");

        final ColorSelectionPanel fillColorPanel = new ColorSelectionPanel();
        final ColorSelectionPanel borderColorPanel = new ColorSelectionPanel();

        layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.LIGHT_GRAY);
        toolPanel.add(layeredPane, "cell 0 0,grow");
        layeredPane.setLayer(fillColorPanel, 1);
        fillColorPanel.setToolTipText("Fill color");
        fillColorPanel.setBorder(new LineBorder(UIManager
            .getColor("CheckBox.light"), 3));
        fillColorPanel.setBounds(18, 23, 35, 35);
        fillColorPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                layeredPane.setLayer(fillColorPanel, 1);
                layeredPane.setLayer(borderColorPanel, 0);

                try
                {
                    Color c = JColorChooser.showDialog(null, "Fill Color",
                        new Color(255, 0, 0));
                    fillColor = new Color(c.getRed(), c.getGreen(),
                        c.getBlue(), c.getAlpha());
                    if (c.getTransparency() != 1.0)
                        opacitySlider.setValue((int) (c.getTransparency() * 10));
                    ((EditorPanel) editorPanel).setColor(fillColor);
                    fillColorPanel.setColor(new Color(c.getRed(), c.getGreen(),
                        c.getBlue(), getOpacitySliderValue()));
                } catch (Exception ex)
                {
                }
            }
        });
        layeredPane.add(fillColorPanel);

        borderColorPanel.setToolTipText("Border color");
        borderColorPanel.setColor(Color.BLACK);
        borderColorPanel.setBorder(new LineBorder(UIManager
            .getColor("CheckBox.light"), 3));
        borderColorPanel.setBounds(3, 8, 35, 35);
        borderColorPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                layeredPane.setLayer(borderColorPanel, 1);
                layeredPane.setLayer(fillColorPanel, 0);
                try
                {
                    Color c = JColorChooser.showDialog(null, "Border Color",
                        new Color(255, 255, 255));
                    borderColor = new Color(c.getRed(), c.getGreen(), c
                        .getBlue(), c.getAlpha());
                    if (c.getTransparency() != 1.0)
                        opacitySlider.setValue((int) (c.getTransparency() * 10));
                    ((EditorPanel) editorPanel).setBorderColor(borderColor);
                    borderColorPanel.setColor(new Color(c.getRed(), c
                        .getGreen(), c.getBlue(), getOpacitySliderValue()));
                } catch (Exception ex)
                {
                }
            }
        });
        layeredPane.add(borderColorPanel);

        opacitySlider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (JLayeredPane.getLayer(fillColorPanel) == 1)
                {
                    ((EditorPanel) editorPanel).setTransparency(getOpacitySliderValue());
                    fillColorPanel.setColor(new Color(fillColorPanel.getColor()
                        .getRed(), fillColorPanel.getColor().getGreen(),
                        fillColorPanel.getColor().getBlue(),
                        getOpacitySliderValue()));
                } else if (JLayeredPane.getLayer(borderColorPanel) == 1)
                {
                    ((EditorPanel) editorPanel).setBorderTransparency(getOpacitySliderValue());
                    borderColorPanel.setColor(new Color(borderColorPanel
                        .getColor().getRed(), borderColorPanel.getColor()
                        .getGreen(), borderColorPanel.getColor().getBlue(),
                        getOpacitySliderValue()));
                }
            }
        });
        opacitySlider.setFont(new Font("Tahoma", Font.PLAIN, 11));
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        opacitySlider.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Opacity Level", TitledBorder.TRAILING,
            TitledBorder.BOTTOM, null, null));
        opacitySlider.setToolTipText("Opacity level");
        opacitySlider.setMajorTickSpacing(25);
        opacitySlider.setValue(255);
        // Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(new Integer(0), new JLabel("0%"));
        labelTable.put(new Integer(25), new JLabel("25%"));
        labelTable.put(new Integer(50), new JLabel("50%"));
        labelTable.put(new Integer(75), new JLabel("75%"));
        labelTable.put(new Integer(100), new JLabel("100%"));
        opacitySlider.setLabelTable(labelTable);

        // Create the label table
        Hashtable<Integer, JLabel> labelTable2 = new Hashtable<>();
        labelTable2.put(new Integer(0), new JLabel("0"));
        labelTable2.put(new Integer(10), new JLabel("1"));
        labelTable2.put(new Integer(20), new JLabel("2"));
        labelTable2.put(new Integer(30), new JLabel("3"));
        labelTable2.put(new Integer(40), new JLabel("4"));
        labelTable2.put(new Integer(50), new JLabel("5"));
        labelTable2.put(new Integer(60), new JLabel("6"));
        labelTable2.put(new Integer(70), new JLabel("7"));
        labelTable2.put(new Integer(80), new JLabel("8"));
        labelTable2.put(new Integer(90), new JLabel("9"));
        labelTable2.put(new Integer(100), new JLabel("10"));

        strokeSlider.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Stroke Size", TitledBorder.TRAILING,
            TitledBorder.BOTTOM, null, null));
        strokeSlider.setMinorTickSpacing(5);
        strokeSlider.setToolTipText("Stroke Width");
        strokeSlider.setMajorTickSpacing(10);
        strokeSlider.setValue(30);
        strokeSlider.setLabelTable(labelTable2);
        strokeSlider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                ((EditorPanel)editorPanel).setStroke(getStrokeSliderValue());
            }
        });

        btnSelect = new JToggleButton("");
        btnSelect.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/select.png")));
        btnSelect.setBackground(Color.WHITE);
        btnSelect.setBorderPainted(false);
        btnSelect.setFocusPainted(false);
        btnSelect.setOpaque(false);
        btnSelect.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("select");
            }
        });
        //btnSelect.setContentAreaFilled(false);
        toolGroup.add(btnSelect);
        toolPanel.add(btnSelect, "cell 3 0");

        JToggleButton pencilButton = new JToggleButton("");
        pencilButton.setBackground(Color.white);
        pencilButton.setFocusPainted(false);
        pencilButton.setSelected(true);
        toolGroup.add(pencilButton);
        pencilButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("pencil");
            }
        });
        pencilButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/pencil.png")));
        toolPanel.add(pencilButton, "cell 4 0");

        JPanel shapePanel = new JPanel();
        shapePanel.setBackground(Color.WHITE);
        shapePanel.setBorder(new TitledBorder(null, "Shapes", TitledBorder.TRAILING, TitledBorder.BOTTOM, null, null));
        toolPanel.add(shapePanel, "cell 5 0");
        shapePanel.setLayout(new GridLayout(0, 2, 0, 0));

        JToggleButton lineButton = new JToggleButton();
        lineButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("line");
            }
        });
        lineButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/line.png")));
        lineButton.setBackground(Color.white);
        lineButton.setFocusPainted(false);
        toolGroup.add(lineButton);
        shapePanel.add(lineButton);

        JToggleButton rectangleButton = new JToggleButton("");
        rectangleButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("rectangle");
            }
        });
        rectangleButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/rectangle.png")));
        rectangleButton.setBackground(Color.white);
        rectangleButton.setFocusPainted(false);
        toolGroup.add(rectangleButton);
        shapePanel.add(rectangleButton);

        JToggleButton roundRectButton = new JToggleButton("");
        roundRectButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("roundRectangle");
            }
        });
        roundRectButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/round_rectangle.png")));
        roundRectButton.setBackground(Color.white);
        roundRectButton.setFocusPainted(false);
        toolGroup.add(roundRectButton);
        shapePanel.add(roundRectButton);

        JToggleButton ellipseButton = new JToggleButton("");
        ellipseButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setTool("ellipse");
            }
        });
        ellipseButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/ellipse.png")));
        ellipseButton.setBackground(Color.white);
        ellipseButton.setFocusPainted(false);
        toolGroup.add(ellipseButton);
        shapePanel.add(ellipseButton);

        chckbxFilled = new JCheckBox("Filled");
        chckbxFilled.setBackground(Color.WHITE);
        chckbxFilled.setFocusPainted(false);
        toolPanel.add(chckbxFilled, "flowy,cell 6 0,growx");

        JPanel editingToolsPanel = new JPanel();
        editingToolsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Tools",
            TitledBorder.TRAILING, TitledBorder.BOTTOM, null, null));
        editingToolsPanel.setBackground(Color.WHITE);
        toolPanel.add(editingToolsPanel, "cell 7 0,grow");
        editingToolsPanel.setLayout(new GridLayout(0, 2, 0, 0));

        textToolBtn = new JButton("T");
        textToolBtn.setFont(new Font("Georgia", Font.BOLD, 16));
        textToolBtn.setToolTipText("Write text");
        textToolBtn.setFocusPainted(false);
        textToolBtn.setFocusable(false);
        textToolBtn.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!((EditorPanel) editorPanel).getTool().equals("text"))
                    ((EditorPanel) editorPanel).setTool("text");
                else
                    ((EditorPanel) editorPanel).setTool("none");
                
                System.out.println("pressed");
                
            }
        });
        editingToolsPanel.add(textToolBtn);

        blurToolBtn = new JButton("");
        blurToolBtn.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/blur.png")));
        blurToolBtn.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).blurSelection();
            }
        });
        blurToolBtn.setFocusPainted(false);
        toolGroup.add(blurToolBtn);
        editingToolsPanel.add(blurToolBtn);

        toggleButton_8 = new JButton("2");
        toggleButton_8.setFocusPainted(false);
        toolGroup.add(toggleButton_8);
        editingToolsPanel.add(toggleButton_8);

        toggleButton_9 = new JButton("3");
        toggleButton_9.setFocusPainted(false);
        toolGroup.add(toggleButton_9);
        editingToolsPanel.add(toggleButton_9);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        toolPanel.add(panel_1, "cell 8 0,grow");
        panel_1.setLayout(new MigLayout("", "[61px]", "[][]"));

        btnUndo = new JButton("");
        btnUndo.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/undo.png")));
        btnUndo.setFocusPainted(false);
        btnUndo.setEnabled(false);
        btnUndo.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).undo();

            }
        });
        //btnUndo.setBackground(Color.WHITE);
        panel_1.add(btnUndo, "cell 0 0,growx,aligny top");

        btnRedo = new JButton("");
        btnRedo.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/redo.png")));
        btnRedo.setFocusPainted(false);
        btnRedo.setEnabled(false);
        btnRedo.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).redo();

            }
        });
        //btnRedo.setBackground(Color.WHITE);
        panel_1.add(btnRedo, "cell 0 1,growx,aligny bottom");

        btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).reset();
            }
        });
        toolPanel.add(btnReset, "cell 9 0,growy");

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                submit();
            }
        });
        toolPanel.add(btnSubmit, "cell 10 0,growy");

        chckbxShadow = new JCheckBox("Shadow");
        chckbxShadow.setFocusPainted(false);
        chckbxShadow.setBackground(Color.WHITE);
        chckbxShadow.setEnabled(false);
        toolPanel.add(chckbxShadow, "cell 6 0");
        chckbxFilled.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                // TODO Auto-generated method stub
                if (chckbxFilled.isSelected())
                {
                    chckbxShadow.setEnabled(true);
                }
                else
                {
                    chckbxShadow.setSelected(false);
                    chckbxShadow.setEnabled(false);
                }

            }
        });

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, "cell 0 1,grow");

        editorPanel = new EditorPanel(image, this);
        editorPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        editorPanel.setBackground(Color.WHITE);
        scrollPane.setViewportView(editorPanel);
        editorPanel.setLayout(new GridLayout(2, 3, 0, 0));
        
        enableTools(false);
        InputMap[] im = { 
            (InputMap)UIManager.get("Button.focusInputMap"),
            (InputMap)UIManager.get("ToggleButton.focusInputMap"),
            (InputMap)UIManager.get("Slider.focusInputMap"),
            (InputMap)UIManager.get("RadioButton.focusInputMap"),
            (InputMap)UIManager.get("TextArea.focusInputMap"),
            (InputMap)UIManager.get("TextField.focusInputMap")
        };
        for(int i = 0; i < im.length; i++)
            im[i].put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    frmEditor.setVisible(true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    protected int getOpacitySliderValue()
    {
        double value = ((opacitySlider.getValue() / 100.0) * 255.0);
        return (int) value;
    }

    protected float getStrokeSliderValue()
    {
        float value = ((strokeSlider.getValue() / 10f));
        return value;
    }

    public void disableRedo()
    {
        btnRedo.setEnabled(false);
    }

    public void enableRedo()
    {
        btnRedo.setEnabled(true);
    }
    public void enableTools(boolean enable)
    {
        blurToolBtn.setEnabled(enable);
        textToolBtn.setEnabled(enable);
        toggleButton_8.setEnabled(enable);
        toggleButton_9.setEnabled(enable);
        
        if(enable == false)
        {
            textToolBtn.setSelected(false);
        }
    }
    public void disableUndo()
    {
        btnUndo.setEnabled(false);
    }

    public void enableUndo()
    {
        btnUndo.setEnabled(true);
    }
    public boolean fill()
    {
        return chckbxFilled.isSelected();
    }

    public boolean shadow()
    {
        return chckbxShadow.isSelected();
    }
    public boolean inSelectionMode()
    {
        return btnSelect.isSelected();
    }

}
