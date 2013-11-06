package editor;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import main.Main;
import net.miginfocom.swing.MigLayout;
import notification.Notification;
import notification.SlidingNotification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import javax.swing.ScrollPaneConstants;
import java.awt.FlowLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import utilities.Save;
import utilities.Upload;

import java.awt.TrayIcon.MessageType;
import javax.swing.JRadioButton;

import overlay.Overlay;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Hashtable;
import javax.swing.border.EmptyBorder;
import javax.swing.JLayeredPane;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.SystemColor;
import javax.swing.JTree;

/*
 * =====KNOWN BUGS=====
 * 
 * Using the hotkey to submit does the submission multiple times
 * 
 */

public class Editor extends JFrame implements MouseMotionListener
{

	private Color fillColor;
	private Color borderColor;
	private JColorChooser colorChooser;
	private EditorPanel editingPanel;
	private Dimension imageDimension;
	private JRadioButton rdbtnUpload;
	private JRadioButton rdbtnSave;
	private JSlider opacitySlider;
	private JSlider strokeSlider;
	private ColorSelectionPanel colorSelection;

	private Upload upload;
	private JButton btnSubmit;
	private JToggleButton textToggleButton;

	private Save save;
	private int mode;

	private KeyboardFocusManager manager;
	private KeyEventDispatcher keyDispatcher;

	/*
	 * KNOWN BUGS
	 */

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)
		{
		}

	}

	/**
	 * Create the application.
	 * 
	 * @param mode
	 */
	public Editor(BufferedImage img, int mode)
	{

		this.addWindowListener(new WindowListener()
		{

			@Override
			public void windowOpened(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				// garbage collect

			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				System.gc();
			}

			@Override
			public void windowActivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub

			}
		});

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Editor.class.getResource("/images/icons/utilities.png")));
		// setType(this.Type.POPUP);

		this.mode = mode;

		/*
		 * this is a much simpler way to handle the key events on the entire
		 * frame without losing focus.
		 */

		keyDispatcher = new KeyEventDispatcher()
		{

			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				int key = e.getKeyCode();
				if (e.getID() == KeyEvent.KEY_PRESSED)
				{
					if (key == KeyEvent.VK_BACK_SPACE)
					{
						editingPanel.backspaceDrawText();
					}
				} 
				else if (e.getID() == KeyEvent.KEY_RELEASED)
				{
					if (e.isShiftDown())
						if (key == KeyEvent.VK_ENTER)
						{
							btnSubmit.doClick();
						}
					if(e.isControlDown())
						if(key == KeyEvent.VK_C)
						{
							editingPanel.copyImageToClipboard();
						}

					if (key == KeyEvent.VK_ESCAPE)
					{
						exit();
					} 
					else if (key == KeyEvent.VK_F10)
					{
						submitToReddit();
					} 
					else
					{
						if (!(key == KeyEvent.VK_CAPS_LOCK
								|| key == KeyEvent.VK_SHIFT
								|| key == KeyEvent.VK_TAB || key == KeyEvent.VK_BACK_SPACE))
							editingPanel.setDrawText(editingPanel.getText()
									+ e.getKeyChar());
						if (e.getKeyCode() == KeyEvent.VK_ENTER
								&& editingPanel.getTool().equals("text"))
						{
							editingPanel.setDrawText(editingPanel.getText()
									+ "\n");
						}
					}

				} else if (e.getID() == KeyEvent.KEY_TYPED)
				{

				}
				return false;
			}

		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(keyDispatcher);

		// manager = KeyboardFocusManager
		// .getCurrentKeyboardFocusManager();
		// manager.addKeyEventDispatcher(new KeyDispatcher());

		fillColor = Color.red;
		colorChooser = new JColorChooser();
		editingPanel = new EditorPanel(img, this);
		editingPanel.setBackground(new Color(0, 0, 0, 0));
		imageDimension = new Dimension(img.getWidth(), img.getHeight()); // get
																			// the
																			// height
																			// of
																			// the
																			// screen
																			// capture
																			// for
																			// the
																			// scrollpane
		this.addMouseMotionListener(this);
		initialize();

		if (this.mode == Overlay.SAVE)
			rdbtnSave.setSelected(true);
		else if (this.mode == Overlay.UPLOAD)
			rdbtnUpload.setSelected(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		colorChooser = new JColorChooser(Color.red);
		ButtonGroup uploadGroup = new ButtonGroup();
		ButtonGroup toolGroup = new ButtonGroup();

		JScrollPane imageScrollPane = new JScrollPane();
		imageScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageScrollPane.setViewportView(editingPanel);
		imageScrollPane.setPreferredSize(imageDimension);
		imageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		imageScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		getContentPane().setLayout(
				new MigLayout("", "[55px][4px][1px][4px][538px,grow]",
						"[67px][4px][1px][4px][321px,grow]"));

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(UIManager.getColor("Button.disabledForeground"));
		getContentPane().add(panel_3, "cell 0 0,grow");
		panel_3.setLayout(null);

		final JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 55, 67);
		layeredPane.setToolTipText("Color Selection");
		layeredPane.setForeground(Color.WHITE);
		layeredPane.setBackground(SystemColor.textInactiveText);
		panel_3.add(layeredPane);

		final ColorSelectionPanel fillColorPanel = new ColorSelectionPanel();
		final ColorSelectionPanel borderColorPanel = new ColorSelectionPanel();
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
					Color c = colorChooser.showDialog(null, "Fill Color",
							new Color(255, 0, 0));
					fillColor = new Color(c.getRed(), c.getGreen(),
							c.getBlue(), c.getAlpha());
					if (c.getTransparency() != 1.0)
						opacitySlider.setValue((int) (c.getTransparency() * 10));
					System.out.println("c.get: " + c.getTransparency());
					editingPanel.setColor(fillColor);
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
					Color c = colorChooser.showDialog(null, "Border Color",
							new Color(255, 255, 255));
					borderColor = new Color(c.getRed(), c.getGreen(), c
							.getBlue(), c.getAlpha());
					if (c.getTransparency() != 1.0)
						opacitySlider.setValue((int) (c.getTransparency() * 10));
					System.out.println("c.get: " + c.getTransparency());
					editingPanel.setBorderColor(borderColor);
					borderColorPanel.setColor(new Color(c.getRed(), c
							.getGreen(), c.getBlue(), getOpacitySliderValue()));
				} catch (Exception ex)
				{
				}
			}
		});
		layeredPane.add(borderColorPanel);

		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		getContentPane().add(separator_2, "cell 2 0 1 5,grow");

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 0 2 5 1,grow");

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, "cell 0 4,grow");
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 10, 0 };
		gbl_panel_1.rowHeights = new int[] { 34, 0, 35, 34, 0, 34, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		final JToggleButton pencilToggleButton = new JToggleButton("", true);
		pencilToggleButton.setToolTipText("Pencil ");
		pencilToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (pencilToggleButton.isSelected())
					editingPanel.setTool("pencil");
				pencilToggleButton.setFocusable(false);
			}
		});
		pencilToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/pencil_selected.png")));
		GridBagConstraints gbc_pencilToggleButton = new GridBagConstraints();
		gbc_pencilToggleButton.fill = GridBagConstraints.BOTH;
		gbc_pencilToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_pencilToggleButton.gridx = 0;
		gbc_pencilToggleButton.gridy = 0;
		panel_1.add(pencilToggleButton, gbc_pencilToggleButton);
		pencilToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/pencil.png")));
		pencilToggleButton.setContentAreaFilled(false);
		pencilToggleButton.setBorder(BorderFactory.createEmptyBorder());
		toolGroup.add(pencilToggleButton);

		final JToggleButton rectToggleButton = new JToggleButton("");
		rectToggleButton.setToolTipText("Rectangle wire frame");
		rectToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (rectToggleButton.isSelected())
					editingPanel.setTool("rectangle");

				rectToggleButton.setFocusable(false);
			}
		});

		final JToggleButton lineToggleButton = new JToggleButton("");
		lineToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/line_selected.png")));
		lineToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/line.png")));
		lineToggleButton.setToolTipText("Draws a line");
		lineToggleButton.setContentAreaFilled(false);
		toolGroup.add(lineToggleButton);
		lineToggleButton.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (lineToggleButton.isSelected())
					editingPanel.setTool("line");
				lineToggleButton.setFocusable(false);

			}
		});
		GridBagConstraints gbc_lineToggleButton = new GridBagConstraints();
		gbc_lineToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_lineToggleButton.gridx = 0;
		gbc_lineToggleButton.gridy = 1;
		panel_1.add(lineToggleButton, gbc_lineToggleButton);
		rectToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/rect_selected.png")));
		rectToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/rect.png")));
		rectToggleButton.setContentAreaFilled(false);
		GridBagConstraints gbc_rectToggleButton = new GridBagConstraints();
		gbc_rectToggleButton.fill = GridBagConstraints.BOTH;
		gbc_rectToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_rectToggleButton.gridx = 0;
		gbc_rectToggleButton.gridy = 2;
		panel_1.add(rectToggleButton, gbc_rectToggleButton);
		toolGroup.add(rectToggleButton);

		final JToggleButton filledToggleButton = new JToggleButton("");
		filledToggleButton.setToolTipText("Filled rectangle");
		filledToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (filledToggleButton.isSelected())
					editingPanel.setTool("filled rectangle");
				filledToggleButton.setFocusable(false);
			}
		});
		filledToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/filled_selected.png")));
		filledToggleButton.setContentAreaFilled(false);
		filledToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/filled.png")));
		GridBagConstraints gbc_filledToggleButton = new GridBagConstraints();
		gbc_filledToggleButton.fill = GridBagConstraints.BOTH;
		gbc_filledToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_filledToggleButton.gridx = 0;
		gbc_filledToggleButton.gridy = 3;
		panel_1.add(filledToggleButton, gbc_filledToggleButton);
		toolGroup.add(filledToggleButton);

		textToggleButton = new JToggleButton("");
		textToggleButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// textToolTip.showBalloon("[Left Click] ",
				// "Left click to submit text entry, and start again.", null);
			}
		});
		textToggleButton
				.setToolTipText("Left click on the image > Start typing");
		textToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (textToggleButton.isSelected())
				{
					editingPanel.setTool("text");
				}
				textToggleButton.setFocusable(false);
			}
		});

		final JToggleButton bRectToggleButton = new JToggleButton("");
		bRectToggleButton.setToolTipText("Bordered rectangle");
		bRectToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (bRectToggleButton.isSelected())
					editingPanel.setTool("bordered rectangle");

				bRectToggleButton.setFocusable(false);
			}
		});
		bRectToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/b_rect_selected.png")));
		bRectToggleButton.setContentAreaFilled(false);
		bRectToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/b_rect.png")));
		GridBagConstraints gbc_bRectToggleButton = new GridBagConstraints();
		gbc_bRectToggleButton.fill = GridBagConstraints.BOTH;
		gbc_bRectToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_bRectToggleButton.gridx = 0;
		gbc_bRectToggleButton.gridy = 4;
		panel_1.add(bRectToggleButton, gbc_bRectToggleButton);
		toolGroup.add(bRectToggleButton);

		JToggleButton blurToggleButton = new JToggleButton("");
		blurToggleButton.setToolTipText("Blur Tool");
		blurToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/blur_selected.png")));
		blurToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/blur.png")));
		blurToggleButton.setContentAreaFilled(false);
		blurToggleButton.setFocusable(false);
		blurToggleButton.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				editingPanel.setTool("blur");

			}
		});
		GridBagConstraints gbc_blurToggleButton = new GridBagConstraints();
		gbc_blurToggleButton.insets = new Insets(0, 0, 5, 0);
		gbc_blurToggleButton.gridx = 0;
		gbc_blurToggleButton.gridy = 5;
		panel_1.add(blurToggleButton, gbc_blurToggleButton);
		toolGroup.add(blurToggleButton);

		textToggleButton.setContentAreaFilled(false);
		textToggleButton.setSelectedIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/text_selected.png")));
		textToggleButton.setIcon(new ImageIcon(Editor.class
				.getResource("/images/icons/buttons/text.png")));
		GridBagConstraints gbc_tglbtnTexttogglebutton = new GridBagConstraints();
		gbc_tglbtnTexttogglebutton.fill = GridBagConstraints.BOTH;
		gbc_tglbtnTexttogglebutton.insets = new Insets(0, 0, 5, 0);
		gbc_tglbtnTexttogglebutton.gridx = 0;
		gbc_tglbtnTexttogglebutton.gridy = 6;
		panel_1.add(textToggleButton, gbc_tglbtnTexttogglebutton);
		toolGroup.add(textToggleButton);

		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.fill = GridBagConstraints.BOTH;
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 7;
		panel_1.add(separator_3, gbc_separator_3);

		this.getContentPane().add(imageScrollPane,
				"cell 4 4,alignx left,aligny top");

		JPanel northPanel = new JPanel();
		this.getContentPane()
				.add(northPanel, "cell 4 0,alignx left,aligny top");

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Snippet Information",
				TitledBorder.LEFT, TitledBorder.TOP, null, null));
		// northPanel.add(panel_2);
		panel_2.setLayout(new MigLayout("", "[55.00][66.00]", "[45.00][]"));

		JLabel lblWidth = new JLabel("Width: " + imageDimension.getWidth());
		panel_2.add(lblWidth, "cell 0 0");

		JLabel lblHeight = new JLabel("Height: " + imageDimension.getHeight());
		panel_2.add(lblHeight, "cell 1 0");

		JSeparator separator = new JSeparator();
		panel_2.add(separator, "cell 0 1 2 1,grow");
		System.out.println(fillColor);
		GridBagLayout gbl_northPanel = new GridBagLayout();
		gbl_northPanel.columnWidths = new int[] { 161, 183, 64, 65, 0, 0 };
		gbl_northPanel.rowHeights = new int[] { 37, 0 };
		gbl_northPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_northPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		northPanel.setLayout(gbl_northPanel);
		opacitySlider = new JSlider();
		opacitySlider.setMinorTickSpacing(5);
		GridBagConstraints gbc_opacitySlider = new GridBagConstraints();
		gbc_opacitySlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_opacitySlider.anchor = GridBagConstraints.NORTH;
		gbc_opacitySlider.insets = new Insets(0, 0, 0, 5);
		gbc_opacitySlider.gridx = 0;
		gbc_opacitySlider.gridy = 0;
		northPanel.add(opacitySlider, gbc_opacitySlider);
		opacitySlider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (layeredPane.getLayer(fillColorPanel) == 1)
				{
					editingPanel.setTransparency(getOpacitySliderValue());
					fillColorPanel.setColor(new Color(fillColorPanel.getColor()
							.getRed(), fillColorPanel.getColor().getGreen(),
							fillColorPanel.getColor().getBlue(),
							getOpacitySliderValue()));
				} else if (layeredPane.getLayer(borderColorPanel) == 1)
				{
					editingPanel.setBorderTransparency(getOpacitySliderValue());
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
		opacitySlider.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),
				"Opacity Level", TitledBorder.TRAILING, TitledBorder.BELOW_TOP,
				null, null));
		opacitySlider.setToolTipText("Opacity level");
		opacitySlider.setMajorTickSpacing(25);
		opacitySlider.setValue(255);
		// Create the label table
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("0%"));
		labelTable.put(new Integer(25), new JLabel("25%"));
		labelTable.put(new Integer(50), new JLabel("50%"));
		labelTable.put(new Integer(75), new JLabel("75%"));
		labelTable.put(new Integer(100), new JLabel("100%"));
		opacitySlider.setLabelTable(labelTable);

		// Create the label table
		Hashtable labelTable2 = new Hashtable();
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
		strokeSlider = new JSlider();
		strokeSlider.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),
				"Stroke Size", TitledBorder.TRAILING, TitledBorder.BELOW_TOP,
				null, null));
		strokeSlider.setPaintLabels(true);
		strokeSlider.setMinorTickSpacing(5);
		strokeSlider.setToolTipText("Stroke Width");
		strokeSlider.setMajorTickSpacing(10);
		strokeSlider.setPaintTicks(true);
		strokeSlider.setValue(30);
		strokeSlider.setLabelTable(labelTable2);
		strokeSlider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				editingPanel.setStroke(getStrokeSliderValue());
			}
		});
		GridBagConstraints gbc_strokeSlider = new GridBagConstraints();
		gbc_strokeSlider.fill = GridBagConstraints.BOTH;
		gbc_strokeSlider.insets = new Insets(0, 0, 0, 5);
		gbc_strokeSlider.gridx = 1;
		gbc_strokeSlider.gridy = 0;
		northPanel.add(strokeSlider, gbc_strokeSlider);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		northPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		rdbtnUpload = new JRadioButton("Upload");
		GridBagConstraints gbc_rdbtnUpload = new GridBagConstraints();
		gbc_rdbtnUpload.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnUpload.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnUpload.gridx = 0;
		gbc_rdbtnUpload.gridy = 0;
		panel.add(rdbtnUpload, gbc_rdbtnUpload);
		rdbtnUpload.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (rdbtnUpload.isSelected())
					mode = Overlay.UPLOAD;
			}
		});
		uploadGroup.add(rdbtnUpload);

		rdbtnSave = new JRadioButton("Save");
		GridBagConstraints gbc_rdbtnSave = new GridBagConstraints();
		gbc_rdbtnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnSave.gridx = 0;
		gbc_rdbtnSave.gridy = 1;
		panel.add(rdbtnSave, gbc_rdbtnSave);
		rdbtnSave.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (rdbtnSave.isSelected())
					mode = Overlay.SAVE;
			}
		});
		uploadGroup.add(rdbtnSave);

		btnSubmit = new JButton("Submit");
		GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
		gbc_btnSubmit.insets = new Insets(0, 0, 0, 5);
		gbc_btnSubmit.anchor = GridBagConstraints.WEST;
		gbc_btnSubmit.gridx = 3;
		gbc_btnSubmit.gridy = 0;
		northPanel.add(btnSubmit, gbc_btnSubmit);
		btnSubmit.setToolTipText("Hotkey: SHIFT + ENTER");
		btnSubmit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				submit();
			}
		});

		this.setTitle("Editor");
		this.setLocation(100, 100);
		/*
		 * Set the editor dimensions
		 */
		this.setMinimumSize(new Dimension(600, 350));
		if (imageDimension.width > 1024 || imageDimension.height > 576)
		{
			imageScrollPane.setMaximumSize(imageDimension);
			this.setSize(1024, 600);
		} else if (imageDimension.width < 1024 || imageDimension.height < 576)
		{

			imageScrollPane.setMaximumSize(imageDimension);
			this.setSize(imageDimension);
		}
		this.setResizable(true);
		this.setMaximizedBounds(new Rectangle(imageDimension));
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.repaint();

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

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	private BufferedImage getEditedImage()
	{
		return editingPanel.getImage();
	}

	public void submit()
	{
		if (!editingPanel.getText().equals("")) // if there is text that has not
												// been processed yet
			editingPanel.forceDrawText();

		if (mode == 0) // save
		{
			save = new Save();
			save.save(getEditedImage());
		} else if (mode == 1) // upload
		{
			upload = new Upload(getEditedImage(), false);
		}

		exit();
	}

	public void submitToReddit()
	{
		if (!editingPanel.getText().equals("")) // if there is text that has not
												// been processed yet
			editingPanel.forceDrawText();

		upload = new Upload(getEditedImage(), true);
	}

	public void exit()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(keyDispatcher);
		Main.closeCurrentEditor();
	}


}
