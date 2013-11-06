package data;

import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import javax.swing.AbstractAction;

/**
 * 
 * @author Shane
 * 
 *         TODO: Create a load method. create method to edit the prefs file with
 *         new settings upon hitting the "apply" button.
 * 
 */
public class PreferencesUI
{
	/*
	 * Add a preference to enable a "save as" mode along with saving to the
	 * capture directory.
	 */

	private String os = System.getProperty("os.name");
	private JFrame frmPreferences;
	public JTextField directoryField;
	public JCheckBox chckbxEnableEditor;
	public JCheckBox chckbxForceMultisnippetCapture;
	public JCheckBox chckbxAutosaveUploads;
	public JComboBox toolBox;

	private JSONObject pref; // outputs
	private JSONObject prefIn; // inputs
	private JSONParser prefParser;

	private DataUtils dataUtils = new DataUtils();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					PreferencesUI window = new PreferencesUI();
					window.frmPreferences.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PreferencesUI()
	{
		initialize();

		loadPreferences();
		setPrefrences();
	}

	private void loadPreferences()
	{
		/*
		 * Load the preferences from the json file and set the Preferences Class
		 * constants
		 * 
		 * preferences are not saving and loading correctly NOTE: possibly fixed
		 * now
		 */
		System.out.println("Loading preferences...");
		prefParser = new JSONParser();
		try
		{
			Object obj;
			if (os.indexOf("Mac") >= 0) // added check for osx file system
			{
				obj = prefParser.parse(new FileReader(
						Preferences.DATA_FOLDER_PATH_MAC + "/prefs.json"));
			} else
			{ // Windows
				obj = prefParser.parse(new FileReader(
						Preferences.DATA_FOLDER_PATH + "/prefs.json"));
			}
			prefIn = (JSONObject) obj;
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		Preferences.DEFAULT_CAPTURE_DIR = (String) prefIn
				.get("user_capture_dir");
		Preferences.DEFAULT_EDITING_TOOL = (Long) prefIn
				.get("default.editing.tool");
		Preferences.DEFAULT_UPLOAD_PROVIDER = (Long) prefIn
				.get("default.upload.provider");
		Preferences.EDITING_ENABLED = (Boolean) prefIn.get("editing.enabled");
		Preferences.FORCE_MULTI_CAPTURE = (Boolean) prefIn
				.get("force.multi.capture");
		Preferences.AUTO_SAVE_UPLOADS = (Boolean) prefIn
				.get("auto.save.uploads");
	}

	private void setPrefrences()
	{
		System.out.println("Set the prefrences...");
		directoryField.setText(Preferences.DEFAULT_CAPTURE_DIR);
		chckbxEnableEditor.setSelected(Preferences.EDITING_ENABLED);
		chckbxForceMultisnippetCapture
				.setSelected(Preferences.FORCE_MULTI_CAPTURE);
		chckbxAutosaveUploads.setSelected(Preferences.AUTO_SAVE_UPLOADS);
		toolBox.setSelectedIndex((int) Preferences.DEFAULT_EDITING_TOOL);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmPreferences = new JFrame();
		frmPreferences.setIconImage(Toolkit.getDefaultToolkit().getImage(
				PreferencesUI.class.getResource("/images/icons/pref.png")));
		frmPreferences.setTitle("Preferences");
		frmPreferences.setBounds(100, 100, 299, 304);
		frmPreferences
				.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frmPreferences.getContentPane().setLayout(
				new MigLayout("", "[263px]", "[355.00px][20.00][]"));

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		frmPreferences.getContentPane().add(tabbedPane, "cell 0 0,grow");

		JPanel tab1 = new JPanel();
		tabbedPane.addTab("General", null, tab1, null);
		tab1.setLayout(new MigLayout("", "[206.00][44.00]", "[15.00][][][][]"));

		JLabel lblLocalSaveDirectory = new JLabel("Local Save Directory");
		tab1.add(lblLocalSaveDirectory, "cell 0 0");

		directoryField = new JTextField();
		directoryField.setEditable(false);
		tab1.add(directoryField, "flowy,cell 0 1,growx");
		directoryField.setColumns(10);

		JButton directoryButton = new JButton("...");
		directoryButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setSelectedFile(new File(Preferences.DEFAULT_CAPTURE_DIR));
				int option = fc.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION)
				{
					directoryField
							.setText(fc.getSelectedFile().getPath() + "/");
					Preferences.DEFAULT_CAPTURE_DIR = fc.getSelectedFile()
							.getPath() + "/SnippingTool++/";
					// setup the new directory
					new File(Preferences.DEFAULT_CAPTURE_DIR + "/Captures/")
							.mkdir();
					new File(Preferences.DEFAULT_CAPTURE_DIR + "/Uploads/")
							.mkdir();
				}
			}
		});
		tab1.add(directoryButton, "cell 1 1");

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					Desktop.getDesktop().open(
							new File(Preferences.DEFAULT_CAPTURE_DIR));
				} catch (IOException e)
				{
					JOptionPane.showMessageDialog(null, "Unable to open "
							+ Preferences.DEFAULT_CAPTURE_DIR, "Error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		tab1.add(btnOpen, "cell 0 2");

		JSeparator separator = new JSeparator();
		tab1.add(separator, "cell 0 3 2 1,grow");

		JPanel tab2 = new JPanel();
		tabbedPane.addTab("Snippet", null, tab2, null);
		tab2.setLayout(new MigLayout("", "[123.00]", "[][][][][]"));

		chckbxEnableEditor = new JCheckBox("Enable Editor");
		chckbxEnableEditor.setSelected(true);
		tab2.add(chckbxEnableEditor, "cell 0 0");

		chckbxForceMultisnippetCapture = new JCheckBox(
				"Force Multi-Snippet Capture");
		tab2.add(chckbxForceMultisnippetCapture, "cell 0 1");

		chckbxAutosaveUploads = new JCheckBox("Auto-Save Uploads");
		chckbxAutosaveUploads.setSelected(true);
		tab2.add(chckbxAutosaveUploads, "cell 0 2");

		JSeparator separator_1 = new JSeparator();
		tab2.add(separator_1, "cell 0 3,grow");

		JLabel lblDefaultTool = new JLabel("Default Tool: ");
		tab2.add(lblDefaultTool, "flowx,cell 0 4");

		toolBox = new JComboBox();
		toolBox.setModel(new DefaultComboBoxModel(new String[] { "Pencil",
				"Rectangle", "Filled Rectangle", "Bordered Rectangle" }));
		tab2.add(toolBox, "cell 0 4");

		JPanel tab3 = new JPanel();
		tabbedPane.addTab("Controls/Hotkeys", null, tab3, null);
		tab3.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		// tab3.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[120.00][39.00]",
				"[][][][][][][][][][][][][][]"));

		JLabel lblEditor = new JLabel("Editor");
		lblEditor.setFont(new Font("Tahoma", Font.BOLD, 18));
		panel_1.add(lblEditor, "flowy,cell 0 0");

		JSeparator separator_3 = new JSeparator();
		panel_1.add(separator_3, "cell 0 1 2 1,grow");

		JLabel lblSubmitEdit = new JLabel("Upload/Save Edit");
		lblSubmitEdit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblSubmitEdit, "cell 0 2");

		JLabel lblEnter = new JLabel("SHIFT + ENTER ");
		lblEnter.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblEnter, "cell 1 2");

		JLabel lblSubmitToReddit = new JLabel("Submit to Reddit");
		lblSubmitToReddit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblSubmitToReddit, "cell 0 3");

		JLabel lblCancelcloseEditor = new JLabel("Cancel/Close Editor");
		lblCancelcloseEditor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblCancelcloseEditor, "cell 0 4");

		JLabel lblF10 = new JLabel("F10 ");
		lblF10.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblF10, "cell 1 3");

		JLabel lblEscape = new JLabel("ESCAPE ");
		lblEscape.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblEscape, "cell 1 4");

		JLabel lblGlobalHotkeys = new JLabel("Global Hotkeys");
		lblGlobalHotkeys.setFont(new Font("Tahoma", Font.BOLD, 18));
		panel_1.add(lblGlobalHotkeys, "cell 0 5");

		JSeparator separator_4 = new JSeparator();
		panel_1.add(separator_4, "cell 0 6 2 1,grow");

		JLabel lblUploadSnippet = new JLabel("Upload Snippet");
		lblUploadSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblUploadSnippet, "cell 0 7");

		JLabel lblCtrlShift = new JLabel("CTRL + SHIFT + 1");
		lblCtrlShift.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblCtrlShift, "cell 1 7");

		JLabel lblUploadScreenshot = new JLabel("Upload Screenshot");
		lblUploadScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblUploadScreenshot, "cell 0 8");

		JLabel lblCtrlShift_1 = new JLabel("CTRL + SHIFT + 2");
		lblCtrlShift_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblCtrlShift_1, "cell 1 8");

		JLabel lblUploadClipboard = new JLabel("Upload Clipboard Img");
		lblUploadClipboard.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblUploadClipboard, "cell 0 9");

		JLabel lblCtrlShift_2 = new JLabel("CTRL + SHIFT + X");
		lblCtrlShift_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblCtrlShift_2, "cell 1 9");

		JLabel lblUploadText = new JLabel("Upload Text");
		lblUploadText.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblUploadText, "cell 0 10");

		JLabel lblAltShift = new JLabel("ALT + SHIFT + 1");
		lblAltShift.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblAltShift, "cell 1 10");

		JLabel lblSaveSnippet = new JLabel("Save Snippet");
		lblSaveSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblSaveSnippet, "cell 0 11");

		JLabel lblCtrlShift_3 = new JLabel("CTRL + SHIFT + 3");
		lblCtrlShift_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblCtrlShift_3, "cell 1 11");

		JLabel lblSaveScreenshot = new JLabel("Save Screenshot");
		lblSaveScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_1.add(lblSaveScreenshot, "cell 0 12");

		JLabel lblCtrlshift = new JLabel("CTRL +SHIFT + 4");
		lblCtrlshift.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_1.add(lblCtrlshift, "cell 1 12");

		JScrollPane scrollPane = new JScrollPane(panel_1);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		// scrollPane.setMaximumSize(new Dimension(200,100));
		tab3.add(scrollPane, BorderLayout.EAST);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Upload Data", null, panel, null);
		panel.setLayout(new MigLayout("", "[150.00]", "[][][][]"));

		JLabel lblImgurUploadsLeft = new JLabel("Imgur uploads left: ");
		panel.add(lblImgurUploadsLeft, "flowx,cell 0 0");

		JLabel lblTimeUntilRefresh = new JLabel("Time until reset: ");
		panel.add(lblTimeUntilRefresh, "flowx,cell 0 1");

		JSeparator separator_2 = new JSeparator();
		panel.add(separator_2, "cell 0 2,grow");

		JLabel lblUploadsLeft = new JLabel(dataUtils.getRemainingUploads()
				+ "/50");
		panel.add(lblUploadsLeft, "cell 0 0");

		JLabel lblTimeLeft = new JLabel(dataUtils.getRefreshTimeMins()
				+ " mins");
		panel.add(lblTimeLeft, "cell 0 1");

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				updatePreferences();
				loadPreferences();
				frmPreferences.dispose();
			}
		});
		frmPreferences.getContentPane().add(btnApply, "cell 0 1,alignx right");
		// frmPreferences.pack();
		frmPreferences.setVisible(true);

	}

	private void updatePreferences()
	{
		Preferences.updatePreferences(this);
	}

	private class SwingAction extends AbstractAction
	{
		public SwingAction()
		{
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
		}
	}
}
