package com.shaneisrael.st.ui.imageviewer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.editor.Editor;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.overlay.Overlay;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.utilities.Browser;
import com.shaneisrael.st.utilities.ClipboardUtilities;
import com.shaneisrael.st.utilities.ProgressBarDialog;
import com.shaneisrael.st.utilities.database.DBUniqueKey;

public class ImageViewer extends JFrame implements ListSelectionListener
{
    private static final long serialVersionUID = 4088295625777675677L;
    private JPanel mainContent;
    private ImageViewerImageList imageList;
    private ImageViewerLinkBuilder linkBuilder;
    private JScrollPane imageListScrollpane;
    private BufferedImage currentImage;
    private ImageLinkPair currentModel;
    private ScrollableImageViewPanel imagePanel;
    private JLabel dateLabel;
    private ButtonGroup viewGroup;

    public ImageViewer()
    {
        initialize();
        setupUI();
    }

    private void initialize()
    {
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                disposeCloudData();

            }
        });
        setSize(740, 508);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Snipping Tool++ Image Viewer");
        setIconImage(getIcon());
        setVisible(true);
    }

    private Image getIcon()
    {
        return Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/images/icons/folder.png")
            );
    }

    private void setupUI()
    {
        setLayout(new BorderLayout());
        mainContent = new JPanel();
        mainContent.setLayout(
            new MigLayout("", "[115.00][298.00,grow]", "[28.00][][337,grow][][][]"));
        createCopyButton();
        createEditButton();
        createOpenButton();
        createDeleteButton();
        createImageListViewer();
        createViewButtons();

        add(mainContent, BorderLayout.CENTER);
    }

    private void createViewButtons()
    {
        viewGroup = new ButtonGroup();
        final JRadioButton rdbtnLocal = new JRadioButton("Local");
        rdbtnLocal.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buildLocalList();
                if (imageList.getModel().getSize() > 0)
                {
                    imageList.setSelectedIndex(0);
                }
            }
        });
        mainContent.add(rdbtnLocal, "flowx,cell 0 1");
        final JRadioButton rdbtnCloud = new JRadioButton("Cloud");
        rdbtnCloud.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                if(!DBUniqueKey.isKeysetReserved())
                {
                    Thread t = new Thread(new Runnable()
                    {
                        public void run()
                        {
                            deleteHistoryDirectory();
                            buildCloudHistoryList();
                            
                            if(ProgressBarDialog.hasInstance())
                            {
                                System.out.println("Has instance");
                                ProgressBarDialog.getInstance().dispose();
                            }
                            
                            if (imageList.getModel().getSize() > 0)
                            {
                                imageList.setSelectedIndex(0);
                            }
                        }
                    });
                    t.start();
                    
                    JOptionPane.showMessageDialog(null, "Downloading Images... This may take a couple seconds.",
                        "Fetching Images..", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    rdbtnLocal.setSelected(true);
                    JOptionPane.showMessageDialog(null, "You need to use a registered keyset to access this feature!\n\n"
                        + "If you already registered a keyset, please check that\n"
                        + "it is correct and valid. Preferences > Stats/Keysets",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mainContent.add(rdbtnCloud, "cell 0 1");

        viewGroup.add(rdbtnLocal);
        viewGroup.add(rdbtnCloud);

        rdbtnLocal.setSelected(true);
    }

    protected void disposeCloudData()
    {
        if(linkBuilder != null)
        {
            if(linkBuilder.hasCloudFiles())
            {
                ArrayList<File> files = linkBuilder.getCloudFiles();
                for(File file : files)
                {
                    file.delete();
                }
                files.clear();
            }
        }
    }
    protected void buildCloudHistoryList()
    {
        linkBuilder = new ImageViewerLinkBuilder(false);
        imageList = new ImageViewerImageList(this, linkBuilder);
        imageListScrollpane.setViewportView(imageList);
    }

    protected void buildLocalList()
    {
        linkBuilder = new ImageViewerLinkBuilder(true);
        imageList = new ImageViewerImageList(this, linkBuilder);
        imageListScrollpane.setViewportView(imageList);
    }

    private void createCopyButton()
    {
        final JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ClipboardUtilities.setClipboardImage(getCurrentImage());
                Main.showNotification("copied", STNotificationType.SUCCESS);
            }

        });
        copyButton.setToolTipText("Copy the image to the clipboard");
        mainContent.add(copyButton, "cell 0 0");
    }

    private void createEditButton()
    {
        final JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BufferedImage temp = new BufferedImage(
                    getCurrentImage().getWidth(),
                    getCurrentImage().getHeight(),
                    getCurrentImage().getType());
                temp.setData(getCurrentImage().getData());
                new Editor().initialize(temp, Overlay.UPLOAD);
            }

        });
        editButton.setToolTipText("Send the image to the editor");
        mainContent.add(editButton, "cell 0 0");
    }

    private void createOpenButton()
    {
        final JButton openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (currentModel.hasImgurInfo())
                {
                    Browser.open(currentModel.getImageLink());
                } else
                {
                    try
                    {
                        new ProcessBuilder("explorer.exe", "/select,"
                            + currentModel.getImageFile().getAbsolutePath()).start();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        mainContent.add(openButton, "cell 1 0");
    }

    private void createDeleteButton()
    {
        final JButton openButton = new JButton("Delete");
        openButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (currentModel.hasImgurInfo())
                {
                    Browser.open(currentModel.getDeleteLink());
                } else
                {
                    try
                    {
                        new ProcessBuilder("explorer.exe", "/select,"
                            + currentModel.getImageFile().getAbsolutePath()).start();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        mainContent.add(openButton, "cell 1 0");
    }

    private void createImageListViewer()
    {
        dateLabel = new JLabel("-unknown date-");
        mainContent.add(dateLabel, "cell 1 0");

        imagePanel = new ScrollableImageViewPanel();
        mainContent.add(imagePanel, "cell 1 2,grow");

        imageListScrollpane = new JScrollPane();

        buildLocalList();
        mainContent.add(imageListScrollpane, "cell 0 2,grow");
        if (imageList.getModel().getSize() > 0)
        {
            imageList.setSelectedIndex(0);
        }
    }

    private BufferedImage getCurrentImage()
    {
        return currentImage;
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            ImageLinkPair imageInformation = imageList.getSelectedValue();
            currentModel = imageInformation;
            onModelChanged(imageInformation);
            try
            {
                currentImage = imageInformation.getImage();
                imagePanel.setImage(getCurrentImage());
            } catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,
                    "Could not find the image file at " + imageInformation.getImageFile().getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onModelChanged(ImageLinkPair imageInformation)
    {
        Date lastModified = new Date(imageInformation.getImageFile().lastModified());
        dateLabel.setText(lastModified.toString());
    }
    public static void deleteHistoryDirectory()
    {
        File tempDir = new File(Preferences.getInstance().getCaptureDirectoryRoot() + "/TempHistory");
        if (tempDir.exists())
            tempDir.delete();
    }
}
