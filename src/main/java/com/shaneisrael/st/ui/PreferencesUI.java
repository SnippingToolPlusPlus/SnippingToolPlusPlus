package com.shaneisrael.st.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.data.Locations;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.utilities.database.DBUniqueKey;

/**
 * 
 * @author Shane
 * 
 *         TODO: Create a load method. create method to edit the prefs file with new settings upon hitting the "apply" button.
 * 
 */
public class PreferencesUI
{
    /*
     * Add a preference to enable a "save as" mode along with saving to the
     * capture directory.
     */

    private JFrame frmPreferences;
    private JTextField directoryField;
    private JCheckBox chckbxEnableEditor;
    private JCheckBox chckbxAutosaveUploads;
    private JCheckBox chckbxAlwaysSaveToFTP;
    private JCheckBox chckbxGenerateTimestamp;
    private JCheckBox chckbxDontTrackUseage;
    private JSlider qualitySlider;
    private JTextField hostField;
    private JTextField userField;
    private JTextField portField;
    private JPasswordField passwordField;
    private JTextField ftpPathField;
    public static JPasswordField keyField1;
    public static JPasswordField keyField2;

    /**
     * Create the application.
     */
    public PreferencesUI()
    {
        initialize();

        setPrefrences();
    }

    private void setPrefrences()
    {
        directoryField.setText(Preferences.getInstance().getCaptureDirectoryRoot());
        chckbxEnableEditor.setSelected(Preferences.getInstance().isEditorEnabled());
        chckbxAutosaveUploads.setSelected(Preferences.getInstance().isAutoSaveEnabled());
        qualitySlider.setValue((int) (100 * Preferences.getInstance().getUploadQuality()));
        hostField.setText(Preferences.getInstance().getFTPHost());
        userField.setText(Preferences.getInstance().getFTPUser());
        portField.setText(Preferences.getInstance().getFTPPort());
        passwordField.setText(Preferences.getInstance().getFTPPassword());
        ftpPathField.setText(Preferences.getInstance().getFTPPath());
        chckbxAlwaysSaveToFTP.setSelected(Preferences.getInstance().getFTPUploadAlways());
        chckbxGenerateTimestamp.setSelected(Preferences.getInstance().getFTPGenerateTimestamp());
        keyField1.setText(Preferences.getInstance().getUniqueKey1());
        keyField2.setText(Preferences.getInstance().getUniqueKey2());
        chckbxDontTrackUseage.setSelected(Preferences.getInstance().isTrackingDisabled());
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
        frmPreferences.setBounds(100, 100, 372, 330);
        frmPreferences.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frmPreferences.getContentPane().setLayout(
            new MigLayout("", "[430.00px]", "[244.00px,grow,baseline][20.00,grow]"));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
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
                fc.setSelectedFile(new File(Preferences.getInstance().getCaptureDirectoryRoot()));
                int option = fc.showOpenDialog(null);
                if (option == JFileChooser.APPROVE_OPTION)
                {
                    directoryField.setText(fc.getSelectedFile().getPath() + "/");
                    Preferences.getInstance().setCaptureDirectoryRoot(
                        fc.getSelectedFile().getPath() + "/SnippingTool++/");
                    // setup the new directory
                    new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/Captures/").mkdir();
                    new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/Uploads/").mkdir();
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
                    Desktop.getDesktop().open(new File(Preferences.getInstance().getCaptureDirectoryRoot()));
                } catch (IOException e)
                {
                    JOptionPane.showMessageDialog(null, "Unable to open "
                        + Preferences.getInstance().getCaptureDirectoryRoot(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
        tab1.add(btnOpen, "cell 0 2");

        tab1.add(new JLabel("Preferences Location"), "cell 0 3");
        String preferencesPath = new Locations().getDataDirectory().getAbsolutePath();
        JLabel lblPreferencesPath = new JLabel(preferencesPath);
        lblPreferencesPath.setToolTipText(preferencesPath);
        tab1.add(lblPreferencesPath, "cell 0 4");
        JButton prefOpen = new JButton("Open");
        prefOpen.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    Desktop.getDesktop().open(new Locations().getDataDirectory());
                } catch (IOException e)
                {
                    JOptionPane.showMessageDialog(null, "Unable to open "
                        + new Locations().getDataDirectory(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
        tab1.add(prefOpen, "cell 0 5");

        JSeparator separator = new JSeparator();
        tab1.add(separator, "cell 0 6 2 1,grow");

        JPanel tab2 = new JPanel();
        tabbedPane.addTab("Snippet", null, tab2, null);
        tab2.setLayout(new MigLayout("", "[123.00]", "[][][][24.00][]"));

        chckbxEnableEditor = new JCheckBox("Enable Editor");
        chckbxEnableEditor.setSelected(true);
        tab2.add(chckbxEnableEditor, "cell 0 0");

        chckbxAutosaveUploads = new JCheckBox("Auto-Save Uploads");
        chckbxAutosaveUploads.setSelected(true);
        tab2.add(chckbxAutosaveUploads, "cell 0 1");

        JSeparator separator_1 = new JSeparator();
        tab2.add(separator_1, "cell 0 2,grow");

        JLabel lblDefaultTool = new JLabel("Upload Quality");
        lblDefaultTool.setToolTipText("Reduces file size decreasing the time it takes to upload.");
        lblDefaultTool.setFont(new Font("Tahoma", Font.BOLD, 16));
        tab2.add(lblDefaultTool, "flowx,cell 0 3");

        qualitySlider = new JSlider();
        qualitySlider.setToolTipText("Higher quality = Larger file size");
        qualitySlider.setPaintLabels(true);
        qualitySlider.setValue(100);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setMajorTickSpacing(25);
        tab2.add(qualitySlider, "cell 0 4");

        JPanel tab4 = new JPanel();
        tabbedPane.addTab("FTP", null, tab4, null);
        tab4.setLayout(new MigLayout("", "[84.00][205.00,grow]", "[][][27.00][][][-17.00][][]"));

        JLabel lblHost = new JLabel("Host:");
        tab4.add(lblHost, "flowx,cell 0 0,alignx right");

        hostField = new JTextField();
        tab4.add(hostField, "cell 1 0,growx");
        hostField.setColumns(10);

        JLabel lblUser = new JLabel("User:");
        tab4.add(lblUser, "flowx,cell 0 1,alignx right");

        userField = new JTextField();
        tab4.add(userField, "flowx,cell 1 1,alignx center");
        userField.setColumns(10);

        JLabel lblPassword = new JLabel("Password:");
        tab4.add(lblPassword, "flowx,cell 0 2,alignx trailing");

        JLabel lblPort = new JLabel("Port:");
        tab4.add(lblPort, "cell 1 1");

        portField = new JTextField();
        portField.setText("21");
        tab4.add(portField, "cell 1 1");
        portField.setColumns(10);

        passwordField = new JPasswordField();
        tab4.add(passwordField, "cell 1 2,growx");

        JLabel lblSavePath = new JLabel("Save Path:");
        lblSavePath.setToolTipText("desired upload location");
        tab4.add(lblSavePath, "cell 0 3,alignx trailing");

        ftpPathField = new JTextField();
        ftpPathField.setToolTipText("example ../../var/www/downloads/");
        ftpPathField.addMouseListener(new MouseListener()
        {

            @Override
            public void mouseReleased(MouseEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                ftpPathField.selectAll();
            }
        });
        ftpPathField.selectAll();
        tab4.add(ftpPathField, "cell 1 3,growx");
        ftpPathField.setColumns(10);

        JSeparator separator_5 = new JSeparator();
        tab4.add(separator_5, "cell 0 4 2 1,growx");

        chckbxAlwaysSaveToFTP = new JCheckBox("Backup to FTP");
        chckbxAlwaysSaveToFTP.setToolTipText("Will save a copy of the image to the desired server via FTP");
        tab4.add(chckbxAlwaysSaveToFTP, "cell 1 6");

        chckbxGenerateTimestamp = new JCheckBox("Timestamp as file name");
        chckbxGenerateTimestamp.setSelected(true);
        tab4.add(chckbxGenerateTimestamp, "cell 1 7");

        JPanel tab5 = new JPanel();
        tabbedPane.addTab("Stats/Keysets", null, tab5, null);
        tab5.setLayout(new MigLayout("", "[161.00,grow][165.00,grow]", "[32.00][26.00][][][][][]"));

        JLabel lblNewLabel = new JLabel("Key 1");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        tab5.add(lblNewLabel, "cell 0 0,alignx center");

        JLabel lblKey = new JLabel("Key 2");
        lblKey.setFont(new Font("Tahoma", Font.BOLD, 14));
        tab5.add(lblKey, "cell 1 0,alignx center");

        keyField1 = new JPasswordField();
        keyField1.setHorizontalAlignment(SwingConstants.CENTER);
        keyField1.setToolTipText("Your registered key #1");
        tab5.add(keyField1, "cell 0 1,growx");

        keyField2 = new JPasswordField();
        keyField2.setHorizontalAlignment(SwingConstants.CENTER);
        keyField2.setToolTipText("Your registered key #2");
        tab5.add(keyField2, "cell 1 1,growx");

        JButton btnHelp = new JButton("Help");
        btnHelp.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                JOptionPane.showMessageDialog(null, "<html>" +
                    "+ Keysets are a way to <i><b>anonymously</b></i> identify yourself with\n"
                    + "your Imgur uploads.\n"
                    + "+ Keysets are not passwords.\n"
                    + "+ Keysets should only be known by you. Don't share them.\n"
                    + "+ Each Key in the Keyset can be any word, phrase, or number.\n"
                    + "just make sure that you can remember both. They don't need to\n"
                    + "be complicated. Just easily rememberable."
                    + "\n\n"
                    + "<html><b>[What is a Keyset??]</b></html>\n"
                    + "A Keyset is a way for you to attach an anonymous \"signature\"\n"
                    + "to your Imgur uploads. For example, knowing your unique Keyset\n"
                    + "will allow you in the future, to download an album of all your\n"
                    + "Imgur uploads to any computer you have a Snipping Tool++\n"
                    + "client with your unique Keyset activated in the preferences."
                    + "\n\n"
                    + "<html><b>[Use default keys]</b></html>\n"
                    + "You can click this button to remove your Keyset and use the\n"
                    + "default Keyset. Using the default Keyset means your stats\n"
                    + "data will not be signed by your keyset.\n\n"
                    + "<html><b>[Validate]</b></html>\n"
                    + "Checks whether your currently entered Keyset is registered. This\n"
                    + "is a great way to check that you entered your Keyset correctly.\n\n"
                    + "<html><b>[Register a Keyset]</b></html>\n"
                    + "If you don't already have a Keyset, you will need to register a\n"
                    + "new Keyset if you want access to your personal upload stats and\n"
                    + "/or any other features that may require it."
                    , "What are Keysets?"
                    ,
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tab5.add(btnHelp, "flowx,cell 0 2,alignx left");

        final JButton btnViewKeys = new JButton("show keys");
        btnViewKeys.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {

                if (btnViewKeys.getText().equals("show keys"))
                {
                    keyField1.setEchoChar((char) 0);
                    keyField2.setEchoChar((char) 0);
                    btnViewKeys.setText("hide keys");
                }
                else
                {
                    keyField1.setEchoChar('\u25CF');
                    keyField2.setEchoChar('\u25CF');
                    btnViewKeys.setText("show keys");
                }
            }
        });
        tab5.add(btnViewKeys, "cell 0 2,alignx right");

        JButton btnRegisterKeyset = new JButton("Register a Keyset");
        btnRegisterKeyset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                new RegisterKeysetUI();
            }
        });

        JButton btnValidate = new JButton("Validate");
        btnValidate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                String key1 = new String(keyField1.getPassword());
                String key2 = new String(keyField2.getPassword());

                if (key1.equals("") || key2.equals(""))
                    JOptionPane.showMessageDialog(null, "Your Key fields are empty!", "Empty Fields",
                        JOptionPane.ERROR_MESSAGE);
                else if (DBUniqueKey.validate(key1, key2))
                    JOptionPane.showMessageDialog(null, "Your Keys are valid!");
                else
                    JOptionPane.showMessageDialog(null, "These keys are invalid!", "Invalid Keys!",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnSaveKeys = new JButton("Save");
        btnSaveKeys.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                final JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("Keyset"));
                int value = fc.showSaveDialog(null);
                if (value == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile();
                    try
                    {
                        PrintWriter writer = new PrintWriter(new FileWriter(file));
                        writer.println(new String(keyField1.getPassword()));
                        writer.println(new String(keyField2.getPassword()));
                        writer.close();
                    } catch (FileNotFoundException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });
        tab5.add(btnSaveKeys, "flowx,cell 1 2");
        tab5.add(btnValidate, "cell 0 5,alignx left");

        JButton btnUseDefault = new JButton("Use default keys");
        btnUseDefault.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                keyField1.setText("");
                keyField2.setText("");
            }
        });
        tab5.add(btnUseDefault, "cell 1 5,alignx right");
        tab5.add(btnRegisterKeyset, "cell 0 6,alignx left");

        JButton btnImport = new JButton("Import Keys");
        btnImport.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                final JFileChooser fc = new JFileChooser();
                int value = fc.showOpenDialog(null);
                if (value == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile();
                    try
                    {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line = null;
                        boolean firstPass = true;
                        while ((line = reader.readLine()) != null)
                        {
                            if (firstPass)
                                keyField1.setText(line);
                            else
                                keyField2.setText(line);

                            firstPass = false;
                        }
                        reader.close();
                    } catch (FileNotFoundException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        btnImport.setToolTipText("Import a previously saved Keyset");
        tab5.add(btnImport, "cell 1 2");

        chckbxDontTrackUseage = new JCheckBox("Don't send statistics data");
        tab5.add(chckbxDontTrackUseage, "cell 1 6,alignx right");

        JPanel tab3 = new JPanel();
        tabbedPane.addTab("Controls/Hotkeys", null, tab3, null);
        tab3.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        // tab3.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new MigLayout("", "[189.00,grow,leading][39.00,grow]", "[][][][][][][][15.00][][][][][][17.00][19.00][][][][][]"));

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
        
        JLabel lblMasterUploadsave = new JLabel("Master Upload/Save");
        lblMasterUploadsave.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_1.add(lblMasterUploadsave, "cell 0 7");
        
        JLabel lblPrintScreen = new JLabel("PRINT SCREEN");
        lblPrintScreen.setFont(new Font("Tahoma", Font.BOLD, 12));
        panel_1.add(lblPrintScreen, "cell 1 7");

        JLabel lblUploadSnippet = new JLabel("Upload Snippet");
        lblUploadSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadSnippet, "cell 0 8");

        JLabel lblCtrlShift = new JLabel("CTRL + SHIFT + 1");
        lblCtrlShift.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlShift, "cell 1 8");

        JLabel lblUploadScreenshot = new JLabel("Upload Screenshot");
        lblUploadScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadScreenshot, "cell 0 9");

        JLabel lblCtrlShift_1 = new JLabel("CTRL + SHIFT + 2");
        lblCtrlShift_1.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlShift_1, "cell 1 9");

        JLabel lblUploadClipboard = new JLabel("Upload Clipboard Img");
        lblUploadClipboard.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadClipboard, "cell 0 10");

        JLabel lblCtrlShift_2 = new JLabel("CTRL + SHIFT + X");
        lblCtrlShift_2.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlShift_2, "cell 1 10");

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

        JLabel lblFtpUploadSnippet = new JLabel("FTP Upload Snippet");
        panel_1.add(lblFtpUploadSnippet, "cell 0 13");

        JLabel lblAltshift = new JLabel("ALT +SHIFT + 1");
        lblAltshift.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblAltshift, "cell 1 13");

        JLabel lblFtpUploadScreenshot = new JLabel("FTP Upload Screenshot");
        panel_1.add(lblFtpUploadScreenshot, "cell 0 14");

        JLabel lblAltshift_1 = new JLabel("ALT +SHIFT + 2");
        lblAltshift_1.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblAltshift_1, "cell 1 14");

        JLabel lblOverlayControls = new JLabel("Overlay");
        lblOverlayControls.setFont(new Font("Tahoma", Font.BOLD, 18));
        panel_1.add(lblOverlayControls, "cell 0 15");

        JSeparator separator_2 = new JSeparator();
        panel_1.add(separator_2, "cell 0 16 2 1,growx");

        JLabel lblIncreaseTransparency = new JLabel("Inc. Transparency");
        lblIncreaseTransparency.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblIncreaseTransparency, "cell 0 17");

        JLabel lblMousewheelUp = new JLabel("WHEEL DOWN");
        lblMousewheelUp.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMousewheelUp, "cell 1 17");

        JLabel lblDecreaseTransparency = new JLabel("Dec. Transparency");
        lblDecreaseTransparency.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblDecreaseTransparency, "cell 0 18");

        JLabel lblWheelUp = new JLabel("WHEEL UP");
        lblWheelUp.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblWheelUp, "cell 1 18");

        JLabel lblUpdateOverlay = new JLabel("Update Overlay");
        lblUpdateOverlay.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUpdateOverlay, "cell 0 19");

        JLabel lblMiddleClick = new JLabel("MIDDLE CLICK");
        lblMiddleClick.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMiddleClick, "cell 1 19");
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        // scrollPane.setMaximumSize(new Dimension(200,100));
        tab3.add(scrollPane, BorderLayout.EAST);

        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                Preferences.getInstance().checkDirectories();
                Preferences.getInstance().setEditorEnabled(chckbxEnableEditor.isSelected());
                Preferences.getInstance().setAutoSaveEnabled(chckbxAutosaveUploads.isSelected());
                Preferences.getInstance().setCaptureDirectoryRoot(directoryField.getText());
                Preferences.getInstance().setUploadQuality(qualitySlider.getValue() / 100f);
                Preferences.getInstance().setFTPHost(hostField.getText());
                Preferences.getInstance().setFTPUser(userField.getText());
                Preferences.getInstance().setFTPPort(portField.getText());
                Preferences.getInstance().setFTPPassword(new String(passwordField.getPassword()));
                Preferences.getInstance().setFTPPath(ftpPathField.getText());
                Preferences.getInstance().setFTPUploadAlways(chckbxAlwaysSaveToFTP.isSelected());
                Preferences.getInstance().setFTPGenerateTimestamp(chckbxGenerateTimestamp.isSelected());
                Preferences.getInstance().setUniqueKey1(new String(keyField1.getPassword()));
                Preferences.getInstance().setUniqueKey2(new String(keyField2.getPassword()));
                Preferences.getInstance().setTrackingDisabled(chckbxDontTrackUseage.isSelected());
                Preferences.getInstance().setDefaultTool(0);
                frmPreferences.dispose();
            }
        });
        frmPreferences.getContentPane().add(btnApply, "cell 0 1,alignx right");
        frmPreferences.setLocationRelativeTo(null);
        frmPreferences.setVisible(true);

    }
}
