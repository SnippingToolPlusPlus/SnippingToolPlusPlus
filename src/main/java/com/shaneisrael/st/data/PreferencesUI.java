package com.shaneisrael.st.data;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.prefs.Preferences;
import javax.swing.JSlider;

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
    public JTextField directoryField;
    public JCheckBox chckbxEnableEditor;
    public JCheckBox chckbxAutosaveUploads;

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
        frmPreferences.setBounds(100, 100, 314, 325);
        frmPreferences.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frmPreferences.getContentPane().setLayout(new MigLayout("", "[263px]", "[240.00px][20.00]"));

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

        JLabel lblDefaultTool = new JLabel("Quality");
        lblDefaultTool.setFont(new Font("Tahoma", Font.BOLD, 16));
        tab2.add(lblDefaultTool, "flowx,cell 0 3");
        
        JSlider qualitySlider = new JSlider();
        qualitySlider.setToolTipText("Higher quality = Larger file size");
        qualitySlider.setPaintLabels(true);
        qualitySlider.setValue(100);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setMajorTickSpacing(25);
        tab2.add(qualitySlider, "cell 0 4");

        JPanel tab3 = new JPanel();
        tabbedPane.addTab("Controls/Hotkeys", null, tab3, null);
        tab3.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        // tab3.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new MigLayout("", "[120.00][39.00]", "[][][][][][][][][][][][][][][][][]"));

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

        JLabel lblSaveSnippet = new JLabel("Save Snippet");
        lblSaveSnippet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblSaveSnippet, "cell 0 10");

        JLabel lblCtrlShift_3 = new JLabel("CTRL + SHIFT + 3");
        lblCtrlShift_3.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlShift_3, "cell 1 10");

        JLabel lblSaveScreenshot = new JLabel("Save Screenshot");
        lblSaveScreenshot.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblSaveScreenshot, "cell 0 11");

        JLabel lblCtrlshift = new JLabel("CTRL +SHIFT + 4");
        lblCtrlshift.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblCtrlshift, "cell 1 11");

        JScrollPane scrollPane = new JScrollPane(panel_1);
        
        JLabel lblOverlayControls = new JLabel("Overlay");
        lblOverlayControls.setFont(new Font("Tahoma", Font.BOLD, 18));
        panel_1.add(lblOverlayControls, "cell 0 12");
        
        JSeparator separator_2 = new JSeparator();
        panel_1.add(separator_2, "cell 0 13 2 1,growx");
        
        JLabel lblIncreaseTransparency = new JLabel("Inc. Transparency");
        lblIncreaseTransparency.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblIncreaseTransparency, "cell 0 14");
        
        JLabel lblMousewheelUp = new JLabel("WHEEL DOWN");
        lblMousewheelUp.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMousewheelUp, "cell 1 14");
        
        JLabel lblDecreaseTransparency = new JLabel("Dec. Transparency");
        lblDecreaseTransparency.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblDecreaseTransparency, "cell 0 15");
        
        JLabel lblWheelUp = new JLabel("WHEEL UP");
        lblWheelUp.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblWheelUp, "cell 1 15");
        
        JLabel lblUpdateOverlay = new JLabel("Update Overlay");
        lblUpdateOverlay.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel_1.add(lblUpdateOverlay, "cell 0 16");
        
        JLabel lblMiddleClick = new JLabel("MIDDLE CLICK");
        lblMiddleClick.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel_1.add(lblMiddleClick, "cell 1 16");
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
                //Preferences.getInstance().setUploadQuality(qualitySlider.getValue() / 100f);
                Preferences.getInstance().setDefaultTool(0);
                frmPreferences.dispose();
            }
        });
        frmPreferences.getContentPane().add(btnApply, "cell 0 1,alignx right");
        // frmPreferences.pack();
        frmPreferences.setVisible(true);

    }
}
