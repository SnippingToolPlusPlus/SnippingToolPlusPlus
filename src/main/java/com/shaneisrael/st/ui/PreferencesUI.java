package com.shaneisrael.st.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.Config;
import com.shaneisrael.st.data.Locations;
import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.prefs.Hotkeys.Hotkeys;
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
    private JCheckBox chckbxShowBalloonNotifications;
    private JSlider qualitySlider;
    private JTextField hostField;
    private JTextField userField;
    private JTextField portField;

    private JPasswordField passwordField;
    private JTextField ftpPathField;
    public static JPasswordField keyField1;
    public static JPasswordField keyField2;
    private int hotkeyCodes[];
    private int hotkeyMods1[];
    private int hotkeyMods2[];
    private Hotkeys hotkeys;
    

    /** HOTKEY FIELDS **/
    private JTextField upKeyField;
    private JTextField upScreenKeyField;
    private JTextField upClipKeyField;
    private JTextField saveKeyField;
    private JTextField saveScreenKeyField;
    private JTextField ftpKeyField;
    private JTextField ftpScreenKeyField;
    private JTextField upActiveWinField;
    private JTextField saveActiveWinField;

    private JComboBox<String> upModBox1;
    private JComboBox<String> upModBox2;
    private JComboBox<String> upScreenModBox1;
    private JComboBox<String> upScreenModBox2;
    private JComboBox<String> saveModBox1;
    private JComboBox<String> saveModBox2;
    private JComboBox<String> saveScreenModBox1;
    private JComboBox<String> saveScreenModBox2;
    private JComboBox<String> upClipModBox1;
    private JComboBox<String> upClipModBox2;
    private JComboBox<String> ftpModBox1;
    private JComboBox<String> ftpModBox2;
    private JComboBox<String> ftpScreenModBox1;
    private JComboBox<String> ftpScreenModBox2;
    private JComboBox<String> upActiveWinModBox1;
    private JComboBox<String> upActiveWinModBox2;
    private JComboBox<String> saveActiveWinModBox1;
    private JComboBox<String> saveActiveWinModBox2;
    private JComboBox<String> providerBox;

    /** END HOTKEY FIELDS **/

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
        providerBox.setSelectedIndex(Preferences.getInstance().getPrimaryProvider());
        chckbxShowBalloonNotifications.setSelected(Preferences.getInstance().showBalloonMessages());
        
        try {
            loadHotkeysFromPrefs();
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.Log(e);
            Preferences.getInstance().setDefaultPreferences();
            loadHotkeysFromPrefs();
        }
    }

    private void loadHotkeysFromPrefs()
    {
        hotkeyCodes = Preferences.getInstance().getHotkeyCodes();
        hotkeyMods1 = Preferences.getInstance().getFirstHotkeyMods();
        hotkeyMods2 = Preferences.getInstance().getSecondHotkeyMods();
        
        upKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[0]));
        upScreenKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[1]));
        upClipKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[4]));
        saveKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[2]));
        saveScreenKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[3]));
        ftpKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[5]));
        ftpScreenKeyField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[6]));
        upActiveWinField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[7]));
        saveActiveWinField.setText(Hotkeys.getKeyCharacter(hotkeyCodes[8]));

        upModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[0]));
        upModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[0]));
        upScreenModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[1]));
        upScreenModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[1]));
        saveModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[2]));
        saveModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[2]));
        saveScreenModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[3]));
        saveScreenModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[3]));
        upClipModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[4]));
        upClipModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[4]));
        ftpModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[5]));
        ftpModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[5]));
        ftpScreenModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[6]));
        ftpScreenModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[6]));
        upActiveWinModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[7]));
        upActiveWinModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[7]));
        saveActiveWinModBox1.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods1[8]));
        saveActiveWinModBox2.setSelectedIndex(Hotkeys.getModBoxIndex(hotkeyMods2[8]));
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmPreferences = new JFrame();
        frmPreferences.getContentPane().setBackground(Color.WHITE);
        frmPreferences.getContentPane().setEnabled(false);
        frmPreferences.setIconImage(Toolkit.getDefaultToolkit().getImage(
            PreferencesUI.class.getResource("/images/icons/pref.png")));
        frmPreferences.setTitle("Preferences");
        frmPreferences.setBounds(100, 100, 444, 687);
        frmPreferences.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frmPreferences.getContentPane().setLayout(
            new MigLayout("", "[430.00px]", "[244.00px,grow,baseline][][20.00,grow]"));

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.setBackground(Color.WHITE);
        frmPreferences.getContentPane().add(tabbedPane, "cell 0 0,growy");

        JPanel tab1 = new JPanel();
        tab1.setBackground(Color.WHITE);
        tabbedPane.addTab("General", null, tab1, null);
        tab1.setLayout(new MigLayout("", "[206.00][44.00]", "[15.00][][][][][][][]"));

        JLabel lblLocalSaveDirectory = new JLabel("Local Save Directory");
        tab1.add(lblLocalSaveDirectory, "cell 0 0");

        directoryField = new JTextField();
        directoryField.setBackground(Color.WHITE);
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
                    Logger.Log(e);
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
                    Logger.Log(e);
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
        
        chckbxShowBalloonNotifications = new JCheckBox("Show Balloon Notifications");
        chckbxShowBalloonNotifications.setSelected(true);
        chckbxShowBalloonNotifications.setBackground(Color.WHITE);
        tab1.add(chckbxShowBalloonNotifications, "cell 0 7");

        JPanel tab2 = new JPanel();
        tab2.setBackground(Color.WHITE);
        tabbedPane.addTab("Snippet", null, tab2, null);
        tab2.setLayout(new MigLayout("", "[123.00,grow]", "[][][][24.00][][][27.00][]"));

        chckbxEnableEditor = new JCheckBox("Enable Editor");
        chckbxEnableEditor.setBackground(Color.WHITE);
        chckbxEnableEditor.setSelected(true);
        tab2.add(chckbxEnableEditor, "cell 0 0");

        chckbxAutosaveUploads = new JCheckBox("Auto-Save Uploads");
        chckbxAutosaveUploads.setBackground(Color.WHITE);
        chckbxAutosaveUploads.setSelected(true);
        tab2.add(chckbxAutosaveUploads, "cell 0 1");

        JSeparator separator_1 = new JSeparator();
        tab2.add(separator_1, "cell 0 2,grow");

        JLabel lblDefaultTool = new JLabel("Upload Quality");
        lblDefaultTool.setToolTipText("Reduces file size decreasing the time it takes to upload.");
        lblDefaultTool.setFont(new Font("Tahoma", Font.BOLD, 16));
        tab2.add(lblDefaultTool, "flowx,cell 0 3");

        qualitySlider = new JSlider();
        qualitySlider.setBackground(Color.WHITE);
        qualitySlider.setToolTipText("Higher quality = Larger file size");
        qualitySlider.setPaintLabels(true);
        qualitySlider.setValue(100);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setMajorTickSpacing(25);
        tab2.add(qualitySlider, "cell 0 4");
        
        JSeparator separator_7 = new JSeparator();
        tab2.add(separator_7, "cell 0 5,growx");
        
        JLabel lblPrimaryImageHost = new JLabel("Primary Image Host");
        lblPrimaryImageHost.setToolTipText("Reduces file size decreasing the time it takes to upload.");
        lblPrimaryImageHost.setFont(new Font("Tahoma", Font.PLAIN, 16));
        tab2.add(lblPrimaryImageHost, "flowx,cell 0 6");
        
        providerBox = new JComboBox();
        providerBox.setModel(new DefaultComboBoxModel(new String[] {"Imgur.com", "Stppl.us"}));
        tab2.add(providerBox, "cell 0 7,alignx left");

        JPanel tab4 = new JPanel();
        tab4.setBackground(Color.WHITE);
        tabbedPane.addTab("FTP", null, tab4, null);
        tab4.setLayout(new MigLayout("", "[84.00][266.00]", "[][][27.00][][][-17.00][][]"));

        JLabel lblHost = new JLabel("Host:");
        tab4.add(lblHost, "flowx,cell 0 0,alignx right");

        hostField = new JTextField();
        tab4.add(hostField, "cell 1 0,growx");
        hostField.setColumns(10);

        JLabel lblUser = new JLabel("User:");
        tab4.add(lblUser, "flowx,cell 0 1,alignx right");

        userField = new JTextField();
        tab4.add(userField, "flowx,cell 1 1,alignx left");
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
        chckbxAlwaysSaveToFTP.setBackground(Color.WHITE);
        chckbxAlwaysSaveToFTP.setToolTipText("Will save a copy of the image to the desired server via FTP");
        tab4.add(chckbxAlwaysSaveToFTP, "cell 1 6");

        chckbxGenerateTimestamp = new JCheckBox("Timestamp as file name");
        chckbxGenerateTimestamp.setBackground(Color.WHITE);
        chckbxGenerateTimestamp.setSelected(true);
        tab4.add(chckbxGenerateTimestamp, "cell 1 7");

        JPanel tab5 = new JPanel();
        tab5.setBackground(Color.WHITE);
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
            @Override
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
            @Override
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
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                new RegisterKeysetUI();
            }
        });

        JButton btnSaveKeys = new JButton("Save");
        btnSaveKeys.addActionListener(new ActionListener()
        {
            @Override
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
                        Logger.Log(e);
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        Logger.Log(e);
                        e.printStackTrace();
                    }

                }
            }
        });
        tab5.add(btnSaveKeys, "flowx,cell 1 2");

        JButton btnValidate = new JButton("Validate");
        btnValidate.addActionListener(new ActionListener()
        {
            @Override
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
        tab5.add(btnValidate, "cell 0 4,alignx left");
        tab5.add(btnRegisterKeyset, "cell 0 6,alignx left");

        JButton btnImport = new JButton("Import Keys");
        btnImport.addActionListener(new ActionListener()
        {
            @Override
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
                        Logger.Log(e);
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        Logger.Log(e);
                        e.printStackTrace();
                    }
                }
            }
        });
        btnImport.setToolTipText("Import a previously saved Keyset");
        tab5.add(btnImport, "cell 1 2");

        chckbxDontTrackUseage = new JCheckBox("Don't send statistics data");
        chckbxDontTrackUseage.setBackground(Color.WHITE);
        tab5.add(chckbxDontTrackUseage, "cell 1 6,alignx right");

        JButton btnUseDefault = new JButton("Use default keys");
        btnUseDefault.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                keyField1.setText("");
                keyField2.setText("");
            }
        });
        tab5.add(btnUseDefault, "cell 0 5,alignx left");

        JPanel tab3 = new JPanel();
        tabbedPane.addTab("Controls/Hotkeys", null, tab3, null);
        tab3.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        // tab3.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new MigLayout("", "[31.00,leading][83.00,grow,leading][81.00,grow][]", "[][][][][][][][][][][][][][][][17.00][19.00][][][][][][][][][]"));

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
        
        JLabel lblCopyImageTo = new JLabel("Copy Image");
        lblCopyImageTo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblCopyImageTo, "cell 0 5");
        
        JLabel lblCtrlc = new JLabel("CTRL+C");
        lblCtrlc.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlc, "cell 1 5");
        
        JLabel lblUndoEdit = new JLabel("Undo Edit");
        lblUndoEdit.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUndoEdit, "cell 0 6");
        
        JLabel lblCtrlz = new JLabel("CTRL+Z");
        lblCtrlz.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlz, "cell 1 6");
        
        JLabel lblRedoEdit = new JLabel("Redo Edit");
        lblRedoEdit.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblRedoEdit, "cell 0 7");
        
        JLabel lblCtrly = new JLabel("CTRL+Y");
        lblCtrly.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrly, "cell 1 7");

        JLabel lblGlobalHotkeys = new JLabel("Global Hotkeys");
        lblGlobalHotkeys.setFont(new Font("Tahoma", Font.BOLD, 18));
        panel_1.add(lblGlobalHotkeys, "cell 0 8");

        JSeparator separator_4 = new JSeparator();
        panel_1.add(separator_4, "cell 0 9 2 1,grow");

        JLabel lblUploadSnippet = new JLabel("Upload Snippet");
        lblUploadSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadSnippet, "cell 0 10,alignx leading");

        upModBox1 = new JComboBox();
        upModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.UPLOAD_SNIPPET_ID] = Hotkeys.getHotkeyMod(upModBox1.getSelectedIndex());
            }
        });
        upModBox1.setModel(new DefaultComboBoxModel(Hotkeys.MODIFIER_KEYS));
        panel_1.add(upModBox1, "flowx,cell 1 10");

        upKeyField = new JTextField();
        upKeyField.setText("NONE");
        upKeyField.setBackground(Color.white);
        upKeyField.setEditable(false);
        upKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                upKeyField.setText(text);
                hotkeyCodes[Config.UPLOAD_SNIPPET_ID] = code;
            }
        });
        upKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                upKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                upKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                upKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(upKeyField, "cell 2 10");
        upKeyField.setColumns(10);

        JLabel lblUploadScreenshot = new JLabel("Upload Screenshot");
        lblUploadScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadScreenshot, "cell 0 11,alignx leading");

        upScreenModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upScreenModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.UPLOAD_SCREEN_ID] = Hotkeys.getHotkeyMod(upScreenModBox1.getSelectedIndex());
            }
        });
        panel_1.add(upScreenModBox1, "flowx,cell 1 11");

        upScreenKeyField = new JTextField();
        upScreenKeyField.setText("NONE");
        upScreenKeyField.setBackground(Color.white);
        upScreenKeyField.setEditable(false);
        upScreenKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                upScreenKeyField.setText(text);
                hotkeyCodes[Config.UPLOAD_SCREEN_ID] = code;
            }
        });
        upScreenKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                upScreenKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                upScreenKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                upScreenKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(upScreenKeyField, "cell 2 11,growx");

        JLabel lblUploadClipboard = new JLabel("Upload Clipboard Img");
        lblUploadClipboard.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUploadClipboard, "cell 0 12,alignx leading");

        upClipModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upClipModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.UPLOAD_CLIPBOARD_ID] = Hotkeys.getHotkeyMod(upClipModBox1.getSelectedIndex());
            }
        });
        panel_1.add(upClipModBox1, "flowx,cell 1 12");

        upClipKeyField = new JTextField();
        upClipKeyField.setText("NONE");
        upClipKeyField.setBackground(Color.white);
        upClipKeyField.setEditable(false);
        upClipKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                upClipKeyField.setText(text);
                hotkeyCodes[Config.UPLOAD_CLIPBOARD_ID] = code;
            }
        });
        upClipKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                upClipKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                upClipKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                upClipKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(upClipKeyField, "cell 2 12,growx");

        JLabel lblSaveSnippet = new JLabel("Save Snippet");
        lblSaveSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblSaveSnippet, "cell 0 13,alignx leading");

        saveModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.SAVE_SNIPPET_ID] = Hotkeys.getHotkeyMod(saveModBox1.getSelectedIndex());
            }
        });
        panel_1.add(saveModBox1, "flowx,cell 1 13");

        saveKeyField = new JTextField();
        saveKeyField.setText("NONE");
        saveKeyField.setBackground(Color.white);
        saveKeyField.setEditable(false);
        saveKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                saveKeyField.setText(text);
                hotkeyCodes[Config.SAVE_SNIPPET_ID] = code;
            }
        });
        saveKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                saveKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                saveKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                saveKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(saveKeyField, "cell 2 13,growx");

        JLabel lblSaveScreenshot = new JLabel("Save Screenshot");
        lblSaveScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblSaveScreenshot, "cell 0 14,alignx leading");

        JScrollPane scrollPane = new JScrollPane(panel_1);

        saveScreenModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveScreenModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.SAVE_SCREEN_ID] = Hotkeys.getHotkeyMod(saveScreenModBox1.getSelectedIndex());
            }
        });
        panel_1.add(saveScreenModBox1, "flowx,cell 1 14");

        saveScreenKeyField = new JTextField();
        saveScreenKeyField.setText("NONE");
        saveScreenKeyField.setBackground(Color.white);
        saveScreenKeyField.setEditable(false);
        saveScreenKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                saveScreenKeyField.setText(text);
                hotkeyCodes[Config.SAVE_SCREEN_ID] = code;
            }
        });
        saveScreenKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                saveScreenKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                saveScreenKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                saveScreenKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(saveScreenKeyField, "cell 2 14,growx");

        JLabel lblFtpUploadSnippet = new JLabel("FTP Upload Snippet");
        panel_1.add(lblFtpUploadSnippet, "cell 0 15,alignx leading");

        ftpModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        ftpModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.FTP_UPLOAD_SNIPPET_ID] = Hotkeys.getHotkeyMod(ftpModBox1.getSelectedIndex());
            }
        });
        panel_1.add(ftpModBox1, "flowx,cell 1 15");

        ftpKeyField = new JTextField();
        ftpKeyField.setText("NONE");
        ftpKeyField.setBackground(Color.white);
        ftpKeyField.setEditable(false);
        ftpKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                ftpKeyField.setText(text);
                hotkeyCodes[Config.FTP_UPLOAD_SNIPPET_ID] = code;
            }
        });
        ftpKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ftpKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                ftpKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                ftpKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(ftpKeyField, "cell 2 15,growx");

        JLabel lblFtpUploadScreenshot = new JLabel("FTP Upload Screen");
        panel_1.add(lblFtpUploadScreenshot, "cell 0 16,alignx leading");

        ftpScreenModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        ftpScreenModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.FTP_UPLOAD_SCREEN_ID] = Hotkeys.getHotkeyMod(ftpScreenModBox1.getSelectedIndex());
            }
        });
        panel_1.add(ftpScreenModBox1, "flowx,cell 1 16");

        ftpScreenKeyField = new JTextField();
        ftpScreenKeyField.setText("NONE");
        ftpScreenKeyField.setBackground(Color.white);
        ftpScreenKeyField.setEditable(false);
        ftpScreenKeyField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                ftpScreenKeyField.setText(text);
                hotkeyCodes[Config.FTP_UPLOAD_SCREEN_ID] = code;
            }
        });
        ftpScreenKeyField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ftpScreenKeyField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                ftpScreenKeyField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                ftpScreenKeyField.setBackground(Color.white);
            }
        });
        panel_1.add(ftpScreenKeyField, "cell 2 16,growx");
        
        JLabel lblUploadActiveWindow = new JLabel("Upload Active Window");
        panel_1.add(lblUploadActiveWindow, "cell 0 17,alignx trailing");
        
        upActiveWinModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upActiveWinModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.UPLOAD_ACTIVE_WINDOW] = Hotkeys.getHotkeyMod(upActiveWinModBox1.getSelectedIndex());
            }
        });
        panel_1.add(upActiveWinModBox1, "flowx,cell 1 17");
        
        upActiveWinField = new JTextField();
        upActiveWinField.setText("NONE");
        upActiveWinField.setEditable(false);
        upActiveWinField.setBackground(Color.WHITE);
        upActiveWinField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                upActiveWinField.setText(text);
                hotkeyCodes[Config.UPLOAD_ACTIVE_WINDOW] = code;
            }
        });
        upActiveWinField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                upActiveWinField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                upActiveWinField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                upActiveWinField.setBackground(Color.white);
            }
        });
        panel_1.add(upActiveWinField, "cell 2 17,growx");
        
        JLabel lblSaveActiveWindow = new JLabel("Save Active Window");
        panel_1.add(lblSaveActiveWindow, "cell 0 18");
        
        saveActiveWinModBox1 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveActiveWinModBox1.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods1[Config.UPLOAD_ACTIVE_WINDOW] = Hotkeys.getHotkeyMod(saveActiveWinModBox1.getSelectedIndex());
            }
        });
        panel_1.add(saveActiveWinModBox1, "flowx,cell 1 18");
        
        saveActiveWinField = new JTextField();
        saveActiveWinField.setText("NONE");
        saveActiveWinField.setEditable(false);
        saveActiveWinField.setBackground(Color.WHITE);
        saveActiveWinField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                String text = Hotkeys.getKeyCharacter(e.getKeyCode()).toUpperCase();
                int code = e.getKeyCode();
                if (text.equals("NONE"))
                    code = Hotkeys.NO_HOTKEY;

                saveActiveWinField.setText(text);
                hotkeyCodes[Config.SAVE_ACTIVE_WINDOW] = code;
            }
        });
        saveActiveWinField.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                saveActiveWinField.selectAll();
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                saveActiveWinField.setBackground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                saveActiveWinField.setBackground(Color.white);
            }
        });
        panel_1.add(saveActiveWinField, "cell 2 18,growx");

        JLabel lblOverlayControls = new JLabel("Overlay");
        lblOverlayControls.setFont(new Font("Tahoma", Font.BOLD, 18));
        panel_1.add(lblOverlayControls, "cell 0 19");

        JSeparator separator_2 = new JSeparator();
        panel_1.add(separator_2, "cell 0 20 2 1,growx");

        JLabel lblZoomMagnifierInout = new JLabel("Magnifier Zoom");
        lblZoomMagnifierInout.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblZoomMagnifierInout, "cell 0 21");

        JLabel lblMouseWheel = new JLabel("MOUSE WHEEL");
        lblMouseWheel.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMouseWheel, "cell 1 21");

        JLabel lblIncreaseTransparency = new JLabel("Overlay Opacity");
        lblIncreaseTransparency.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblIncreaseTransparency, "cell 0 22");

        JLabel lblMousewheelUp = new JLabel("MOUSE WHEEL");
        lblMousewheelUp.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMousewheelUp, "cell 1 22");

        JSeparator separator_6 = new JSeparator();
        panel_1.add(separator_6, "cell 0 24 2 1,grow");

        JTextPane txtpnNote = new JTextPane();
        txtpnNote.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtpnNote.setBackground(Color.WHITE);
        txtpnNote.setEditable(false);
        txtpnNote
            .setText("* Note - To change overlay transparency, you must first hide the Magnifying Glass with [Right Click].");
        panel_1.add(txtpnNote, "cell 0 25,growx");

        JLabel lblUpdateOverlay = new JLabel("Update Overlay");
        lblUpdateOverlay.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUpdateOverlay, "cell 0 23");

        JLabel lblMiddleClick = new JLabel("MIDDLE CLICK");
        lblMiddleClick.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMiddleClick, "cell 1 23");

        JLabel label = new JLabel("+");
        panel_1.add(label, "cell 1 10");

        upModBox2 = new JComboBox();
        upModBox2.setModel(new DefaultComboBoxModel(Hotkeys.MODIFIER_KEYS));
        upModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.UPLOAD_SNIPPET_ID] = Hotkeys.getHotkeyMod(upModBox2.getSelectedIndex());
            }
        });
        panel_1.add(upModBox2, "cell 1 10");

        JLabel label_1 = new JLabel("+");
        panel_1.add(label_1, "cell 1 10");

        JLabel label_2 = new JLabel("+");
        panel_1.add(label_2, "cell 1 11");

        upScreenModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upScreenModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.UPLOAD_SCREEN_ID] = Hotkeys.getHotkeyMod(upScreenModBox2.getSelectedIndex());
            }
        });
        panel_1.add(upScreenModBox2, "cell 1 11");

        JLabel label_3 = new JLabel("+");
        panel_1.add(label_3, "cell 1 11");

        JLabel label_4 = new JLabel("+");
        panel_1.add(label_4, "cell 1 12");

        upClipModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upClipModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.UPLOAD_CLIPBOARD_ID] = Hotkeys.getHotkeyMod(upClipModBox2.getSelectedIndex());
            }
        });
        panel_1.add(upClipModBox2, "cell 1 12");

        JLabel label_5 = new JLabel("+");
        panel_1.add(label_5, "cell 1 12");

        JLabel label_6 = new JLabel("+");
        panel_1.add(label_6, "cell 1 13");

        saveModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.SAVE_SNIPPET_ID] = Hotkeys.getHotkeyMod(saveModBox2.getSelectedIndex());
            }
        });
        panel_1.add(saveModBox2, "cell 1 13");

        JLabel label_7 = new JLabel("+");
        panel_1.add(label_7, "cell 1 13");

        JLabel label_8 = new JLabel("+");
        panel_1.add(label_8, "cell 1 14");

        saveScreenModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveScreenModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.SAVE_SCREEN_ID] = Hotkeys.getHotkeyMod(saveScreenModBox2.getSelectedIndex());
            }
        });
        panel_1.add(saveScreenModBox2, "cell 1 14");

        JLabel label_9 = new JLabel("+");
        panel_1.add(label_9, "cell 1 14");

        JLabel label_10 = new JLabel("+");
        panel_1.add(label_10, "cell 1 15");

        ftpModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        ftpModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.FTP_UPLOAD_SNIPPET_ID] = Hotkeys.getHotkeyMod(ftpModBox2.getSelectedIndex());
            }
        });
        panel_1.add(ftpModBox2, "cell 1 15");

        JLabel label_11 = new JLabel("+");
        panel_1.add(label_11, "cell 1 15");

        JLabel label_12 = new JLabel("+");
        panel_1.add(label_12, "cell 1 16");

        ftpScreenModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        ftpScreenModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                hotkeyMods2[Config.FTP_UPLOAD_SCREEN_ID] = Hotkeys.getHotkeyMod(ftpScreenModBox2.getSelectedIndex());
            }
        });
        panel_1.add(ftpScreenModBox2, "cell 1 16");
        
        

        JLabel label_13 = new JLabel("+");
        panel_1.add(label_13, "cell 1 16");
        
        JLabel label_14 = new JLabel("+");
        panel_1.add(label_14, "cell 1 18");
        
        saveActiveWinModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        saveActiveWinModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent arg0)
            {
                hotkeyMods2[Config.SAVE_ACTIVE_WINDOW] = Hotkeys.getHotkeyMod(saveActiveWinModBox2.getSelectedIndex());                
            }
        });
        panel_1.add(saveActiveWinModBox2, "cell 1 18");
        
        JLabel label_15 = new JLabel("+");
        panel_1.add(label_15, "cell 1 18");
        
        JLabel label_16 = new JLabel("+");
        panel_1.add(label_16, "cell 1 17");
        
        upActiveWinModBox2 = new JComboBox(Hotkeys.MODIFIER_KEYS);
        upActiveWinModBox2.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent arg0)
            {
                hotkeyMods2[Config.UPLOAD_ACTIVE_WINDOW] = Hotkeys.getHotkeyMod(upActiveWinModBox2.getSelectedIndex());                
            }
        });
        panel_1.add(upActiveWinModBox2, "cell 1 17");
        
        JLabel label_17 = new JLabel("+");
        panel_1.add(label_17, "cell 1 17");
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        // scrollPane.setMaximumSize(new Dimension(200,100));
        tab3.add(scrollPane, BorderLayout.CENTER);

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
                Preferences.getInstance().setHotkeyCodes(hotkeyCodes);
                Preferences.getInstance().setFirstHotkeyMods(hotkeyMods1);
                Preferences.getInstance().setSecondHotkeyMods(hotkeyMods2);
                Preferences.getInstance().setPrimaryProvider(providerBox.getSelectedIndex());
                Preferences.getInstance().showBalloonMessages(chckbxShowBalloonNotifications.isSelected());
                
                hotkeys = new Hotkeys();
                hotkeys.registerHotkeys();

                frmPreferences.dispose();
            }

        });
        frmPreferences.getContentPane().add(btnApply, "cell 0 2,alignx right");
        frmPreferences.setLocationRelativeTo(null);
        frmPreferences.setVisible(true);

    }

    
}
