package com.shaneisrael.st.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.overlay.Overlay;
import com.shaneisrael.st.upload.SimpleFTPUploader;
import com.shaneisrael.st.utilities.ClipboardUtilities;
import com.shaneisrael.st.utilities.ImageUtilities;
import com.shaneisrael.st.utilities.Save;
import com.shaneisrael.st.utilities.Upload;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;

public class Editor
{

    private JPanel editorPanel;
    private JButton btnReset, blurToolBtn, textToolBtn;
    private JMenuItem mntmRedo, mntmUndo;
    private JToggleButton btnSelect;
    private JCheckBox chckbxShadow, chckbxFilled;
    JFrame frmEditor;
    private Color fillColor = Color.red;
    private Color borderColor = Color.black;
    private JColorChooser colorChooser;
    private JLayeredPane layeredPane;

    private ButtonGroup toolGroup;
    private BufferedImage image;
    private int mode;
    private Save save;
    private Upload upload;

    private KeyEventDispatcher keyDispatcher;
    private static Editor instance = null;
    private JMenuBar menuBar;
    private JPopupMenu popupMenu;
    private JRadioButtonMenuItem rdbtnmntmPlain;
    private JRadioButtonMenuItem rdbtnmntmBold;
    private JRadioButtonMenuItem rdbtnmntmItalic;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField opacityField;
    private JTextField strokeField;
    private JCheckBox chckbxDashed;
    private JMenuItem mntmFontStyle;
    private JMenuItem mntmFontSize_1;

    /**
     * Create the application.
     */
    public Editor()
    {

    }

    private BufferedImage getEditedImage()
    {
        return ((EditorPanel) editorPanel).getImage();
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
    
    public void submitToReddit()
    {
        upload = new Upload(getEditedImage(), true);
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
            instance.dispose();
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
        frmEditor.setType(Type.NORMAL);
        frmEditor.setTitle("Editor [Beta]");
        frmEditor.setMinimumSize(new Dimension(900, 400));
        frmEditor.setSize(img.getWidth()+40, img.getHeight()+200);
        frmEditor.setIconImage(Toolkit.getDefaultToolkit().getImage(
            Editor.class.getResource("/images/icons/utilities.png")));
        frmEditor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        

        keyDispatcher = new KeyEventDispatcher()
        {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                if (e.isControlDown())
                {
                    if (e.getID() == KeyEvent.KEY_PRESSED)
                    {
                        switch (e.getKeyCode()) {
                        case KeyEvent.VK_Z:
                            ((EditorPanel) editorPanel).undo();
                            break;
                        case KeyEvent.VK_Y:
                            ((EditorPanel) editorPanel).redo();
                            break;
                        case KeyEvent.VK_C:
                            copyImageToClipboard();
                            break;
                        }
                    }
                }
                else if (e.isShiftDown())
                {
                    if (e.getID() == KeyEvent.KEY_TYPED)
                    {
                        if (((EditorPanel) editorPanel).getTool().equals("text") && inSelectionMode())
                        {
                            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                                ((EditorPanel) editorPanel).backspaceWriteText();
                            else if (e.getKeyChar() == KeyEvent.VK_SPACE)
                                ((EditorPanel) editorPanel).addWriteText(' ');
                            else if (e.getKeyChar() == KeyEvent.VK_ENTER)
                                ((EditorPanel) editorPanel).submitText();
                            else
                                ((EditorPanel) editorPanel).addWriteText((e.getKeyChar()));

                        }
                    }
                    if (e.getID() == KeyEvent.KEY_RELEASED)
                    {
                        switch (e.getKeyCode())
                        {
                        case KeyEvent.VK_ENTER:
                            submit();
                            break;

                        }
                    }
                }
                else
                {
                    if (e.getID() == KeyEvent.KEY_TYPED)
                    {
                        if (((EditorPanel) editorPanel).getTool().equals("text") && inSelectionMode())
                        {
                            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                                ((EditorPanel) editorPanel).backspaceWriteText();
                            else if (e.getKeyChar() == KeyEvent.VK_SPACE)
                                ((EditorPanel) editorPanel).addWriteText(' ');
                            else if (e.getKeyChar() == KeyEvent.VK_ENTER)
                                ((EditorPanel) editorPanel).addWriteText('\n');
                            else
                                ((EditorPanel) editorPanel).addWriteText((e.getKeyChar()));

                        }
                    }
                    if (e.getID() == KeyEvent.KEY_RELEASED)
                    {
                        switch (e.getKeyCode())
                        {
                        case KeyEvent.VK_ESCAPE:
                            exit();
                            break;
                        case KeyEvent.VK_F10:
                            submitToReddit();
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
        toolPanel.setLayout(new MigLayout("", "[65.00,leading][45.00][246.00,center][43.00][][]", "[60.00,grow,top]"));
        
        JLabel lblStroke = new JLabel("Stroke");
        toolPanel.add(lblStroke, "flowy,cell 1 0,aligny top");
        
        strokeField = new JTextField();
        strokeField.setText("3");
        toolPanel.add(strokeField, "cell 1 0");
        strokeField.setColumns(10);
        strokeField.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String value = strokeField.getText();
                
                if(value.matches("^-?\\d+$"))
                {
                    if (Integer.parseInt(value)<0){
                        strokeField.setText("0");
                    }
                    else if(Integer.parseInt(value)>100)
                    {
                        strokeField.setText("100");
                    }
                    else
                        strokeField.setText(value);
                }
                else
                {
                    strokeField.setText("100");
                }
                
                ((EditorPanel) editorPanel).setStroke(getStrokeSliderValue());
            }
        });
        strokeField.addFocusListener(new FocusListener()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                update();
                
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            public void update() {
                String value = strokeField.getText();
                
                if(value.matches("^-?\\d+$"))
                {
                    if (Integer.parseInt(value)<0){
                        strokeField.setText("0");
                    }
                    else if(Integer.parseInt(value)>100)
                    {
                        strokeField.setText("100");
                    }
                    else
                        strokeField.setText(value);
                }
                else
                {
                    strokeField.setText("100");
                }
                
                ((EditorPanel) editorPanel).setStroke(getStrokeSliderValue());
            }
        });
        
        JLabel lblOpacity = new JLabel("Opacity");
        toolPanel.add(lblOpacity, "cell 1 0");
        
        opacityField = new JTextField();
        opacityField.setHorizontalAlignment(SwingConstants.LEFT);
        opacityField.setText("100%");
        toolPanel.add(opacityField, "cell 1 0");
        opacityField.setColumns(10);

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
                        opacityField.setText(""+c.getTransparency() * 10);
                    ((EditorPanel) editorPanel).setColor(fillColor);
                    fillColorPanel.setColor(new Color(c.getRed(), c.getGreen(),
                        c.getBlue(), getOpacitySliderValue()));
                } catch (Exception ex)
                {
                    Logger.Log(ex);
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
                        opacityField.setText(""+c.getTransparency() * 10);
                    ((EditorPanel) editorPanel).setBorderColor(borderColor);
                    borderColorPanel.setColor(new Color(c.getRed(), c
                        .getGreen(), c.getBlue(), getOpacitySliderValue()));
                } catch (Exception ex)
                {
                    Logger.Log(ex);
                }
            }
        });
        layeredPane.add(borderColorPanel);
        // Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(new Integer(0), new JLabel("0%"));
        labelTable.put(new Integer(50), new JLabel("50%"));
        labelTable.put(new Integer(100), new JLabel("100%"));

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

        opacityField.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String value = opacityField.getText();
                if(value.contains("%"))
                    value = value.substring(0, opacityField.getText().length()-1);
                
                if(value.matches("^-?\\d+$"))
                {
                    if (Integer.parseInt(value)<0){
                        opacityField.setText("0%");
                    }
                    else if(Integer.parseInt(value)>100)
                    {
                        opacityField.setText("100%");
                    }
                    else
                        opacityField.setText(value+"%");
                }
                else
                {
                    opacityField.setText("100%");
                }
                
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
        opacityField.addFocusListener(new FocusListener()
        {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                update();
                
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            public void update() {
                String value = opacityField.getText();
                if(value.contains("%"))
                    value = value.substring(0, opacityField.getText().length()-1);
                
                if(value.matches("^-?\\d+$"))
                {
                    if (Integer.parseInt(value)<0){
                        opacityField.setText("0%");
                    }
                    else if(Integer.parseInt(value)>100)
                    {
                        opacityField.setText("100%");
                    }
                    else
                        opacityField.setText(value+"%");
                }
                else
                {
                    opacityField.setText("100%");
                }
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
        
                JToggleButton pencilButton = new JToggleButton("");
                toolPanel.add(pencilButton, "flowx,cell 2 0");
                pencilButton.setVerticalAlignment(SwingConstants.TOP);
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

        JPanel shapePanel = new JPanel();
        shapePanel.setBackground(Color.WHITE);
        toolPanel.add(shapePanel, "cell 2 0,aligny top");
                        
                                JToggleButton rectangleButton = new JToggleButton("");
                                rectangleButton.addActionListener(new ActionListener()
                                {

                                    @Override
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        ((EditorPanel) editorPanel).setTool("rectangle");
                                    }
                                });
                                
                                        JToggleButton lineButton = new JToggleButton();
                                        lineButton.addActionListener(new ActionListener()
                                        {

                                            @Override
                                            public void actionPerformed(ActionEvent e)
                                            {
                                                ((EditorPanel) editorPanel).setTool("line");
                                            }
                                        });
                                        shapePanel.setLayout(new GridLayout(0, 2, 0, 0));
                                        lineButton.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/line.png")));
                                        lineButton.setBackground(Color.white);
                                        lineButton.setFocusPainted(false);
                                        toolGroup.add(lineButton);
                                        shapePanel.add(lineButton);
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
        
        chckbxDashed = new JCheckBox("Dashed");
        chckbxDashed.setBackground(Color.WHITE);
        toolPanel.add(chckbxDashed, "flowy,cell 3 0");

        chckbxFilled = new JCheckBox("Filled");
        chckbxFilled.setBackground(Color.WHITE);
        chckbxFilled.setFocusPainted(false);
        toolPanel.add(chckbxFilled, "cell 3 0,growx,aligny top");

        JPanel editingToolsPanel = new JPanel();
        editingToolsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Tools", TitledBorder.LEADING, TitledBorder.BOTTOM, null, new Color(0, 0, 0)));
        editingToolsPanel.setBackground(Color.WHITE);
        toolPanel.add(editingToolsPanel, "cell 4 0,growx,aligny top");
        editingToolsPanel.setLayout(new GridLayout(0, 3, 0, 0));

        textToolBtn = new JButton("T");
        textToolBtn.setFont(new Font("Georgia", Font.BOLD, 36));
        textToolBtn.setToolTipText("Write text");
        textToolBtn.setFocusPainted(false);
        textToolBtn.setFocusable(false);
        textToolBtn.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!((EditorPanel) editorPanel).getTool().equals("text"))
                    ((EditorPanel) editorPanel).setTool("text");
                else
                    ((EditorPanel) editorPanel).setTool("none");

                System.out.println("pressed");

            }
        });
        
        btnSelect = new JToggleButton("");
        editingToolsPanel.add(btnSelect);
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

        chckbxShadow = new JCheckBox("Shadow");
        chckbxShadow.setFocusPainted(false);
        chckbxShadow.setBackground(Color.WHITE);
        chckbxShadow.setEnabled(false);
        toolPanel.add(chckbxShadow, "cell 3 0");
                
                        btnReset = new JButton("Reset");
                        btnReset.addActionListener(new ActionListener()
                        {

                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                ((EditorPanel) editorPanel).reset();
                            }
                        });
                        toolPanel.add(btnReset, "flowy,cell 5 0,grow");
                
                        JButton btnSubmit = new JButton("Submit");
                        btnSubmit.addActionListener(new ActionListener()
                        {

                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                submit();
                            }
                        });
                        toolPanel.add(btnSubmit, "cell 5 0,growy");
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

        popupMenu = new JPopupMenu();
        addPopup(editorPanel, popupMenu);

        rdbtnmntmPlain = new JRadioButtonMenuItem("Plain");
        rdbtnmntmPlain.setSelected(true);
        rdbtnmntmPlain.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setFontType(Font.PLAIN);
            }
        });
        buttonGroup.add(rdbtnmntmPlain);
        popupMenu.add(rdbtnmntmPlain);

        rdbtnmntmBold = new JRadioButtonMenuItem("Bold");
        rdbtnmntmBold.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setFontType(Font.BOLD);
            }
        });
        buttonGroup.add(rdbtnmntmBold);
        popupMenu.add(rdbtnmntmBold);

        rdbtnmntmItalic = new JRadioButtonMenuItem("Italic");
        rdbtnmntmItalic.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).setFontType(Font.ITALIC);
            }
        });
        buttonGroup.add(rdbtnmntmItalic);
        popupMenu.add(rdbtnmntmItalic);
        
        mntmFontStyle = new JMenuItem("Font Style");
        mntmFontStyle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setFontStyle();
            }
        });
        
        mntmFontSize_1 = new JMenuItem("Font Size");
        mntmFontSize_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setFontSize();
            }
        });
        popupMenu.add(mntmFontSize_1);
        popupMenu.add(mntmFontStyle);
        editorPanel.setLayout(new GridLayout(2, 3, 0, 0));

        menuBar = new JMenuBar();
        frmEditor.setJMenuBar(menuBar);

        JMenu mnFont = new JMenu("Font");
        menuBar.add(mnFont);

        JMenuItem mntmFontSize = new JMenuItem("Font Size");
        mntmFontSize.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setFontSize();
            }
        });
        mnFont.add(mntmFontSize);
        
        JMenuItem mntmFontStyle_1 = new JMenuItem("Font Style");
        mntmFontStyle_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setFontStyle();
            }
        });
        mnFont.add(mntmFontStyle_1);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        mntmUndo = new JMenuItem("Undo [ctrl+z]");
        mntmUndo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).undo();
            }
        });
        mntmUndo.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/undo.png")));
        mnEdit.add(mntmUndo);

        mntmRedo = new JMenuItem("Redo [ctrl+y]");
        mntmRedo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((EditorPanel) editorPanel).redo();
            }
        });
        mntmRedo.setIcon(new ImageIcon(Editor.class.getResource("/images/icons/buttons/redo.png")));
        mnEdit.add(mntmRedo);

        enableTools(false);
        InputMap[] im = {
                (InputMap) UIManager.get("Button.focusInputMap"),
                (InputMap) UIManager.get("ToggleButton.focusInputMap"),
                (InputMap) UIManager.get("Slider.focusInputMap"),
                (InputMap) UIManager.get("RadioButton.focusInputMap"),
                (InputMap) UIManager.get("TextArea.focusInputMap"),
                (InputMap) UIManager.get("TextField.focusInputMap")
        };
        for (int i = 0; i < im.length; i++)
            im[i].put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");

        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    frmEditor.setVisible(true);
                } catch (Exception e)
                {
                    Logger.Log(e);
                    e.printStackTrace();
                }
            }
        });

    }

    protected int getOpacitySliderValue()
    {
        String text = opacityField.getText().replace("%", "");
        int value = Integer.parseInt(text);
        double v = ((value/ 100.0) * 255.0);
        return (int)v;
    }

    protected float getStrokeSliderValue()
    {
        float value = (Integer.parseInt(strokeField.getText()));
        return value;
    }

    public void disableRedo()
    {
        mntmRedo.setEnabled(false);
    }

    public void enableRedo()
    {
        mntmRedo.setEnabled(true);
    }

    public void enableTools(boolean enable)
    {
        blurToolBtn.setEnabled(enable);
        textToolBtn.setEnabled(enable);

        if (enable == false)
        {
            textToolBtn.setSelected(false);
        }
    }

    public void disableUndo()
    {
        mntmUndo.setEnabled(false);
    }

    public void enableUndo()
    {
        mntmUndo.setEnabled(true);
    }

    public boolean fill()
    {
        return chckbxFilled.isSelected();
    }

    public boolean shadow()
    {
        return chckbxShadow.isSelected();
    }
    
    public boolean dashed()
    {
        return chckbxDashed.isSelected();
    }

    public boolean inSelectionMode()
    {
        return btnSelect.isSelected();
    }
    private void copyImageToClipboard()
    {
        BufferedImage temp = ((EditorPanel) editorPanel).getImage();
//        BufferedImage image = new BufferedImage(editorPanel.getI.getWidth(null), graph.getHeight(null),
//            BufferedImage.TYPE_INT_ARGB);
//        finalImage.getGraphics().drawImage(graph, 0, 0, null);
        ClipboardUtilities.setClipboardImage(temp);
    }
    private static void addPopup(Component component, final JPopupMenu popup)
    {
        component.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    showMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e)
            {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
    
    private void setFontStyle()
    {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = e.getAvailableFontFamilyNames(); // Get the fonts
        JComboBox<String> cbox = new JComboBox(fonts);
        JOptionPane.showMessageDialog( null, cbox, "Choose a font", JOptionPane.QUESTION_MESSAGE);
        ((EditorPanel) editorPanel).setFontStyle(cbox.getSelectedItem().toString());
    }
    
    private void setFontSize()
    {
        int size;
        try
        {
            size = Integer.parseInt(JOptionPane.showInputDialog("Desired font size?"));
        }
        catch (NumberFormatException ex)
        {
            Logger.Log(ex);
            size = 16;
        }
        ((EditorPanel) editorPanel).setFontSize(size);
    }
}
