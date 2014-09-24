package com.shaneisrael.st.ui.imageviewer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.Main;
import com.shaneisrael.st.editor.Editor;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.overlay.Overlay;
import com.shaneisrael.st.utilities.Browser;
import com.shaneisrael.st.utilities.ClipboardUtilities;

public class ImageViewer extends JFrame implements ListSelectionListener
{
    private static final long serialVersionUID = 4088295625777675677L;
    private JPanel mainContent;
    private ImageViewerImageList imageList;
    private BufferedImage currentImage;
    private ImageLinkPair currentModel;
    private ScrollableImageViewPanel imagePanel;
    private JLabel dateLabel;

    public ImageViewer()
    {
        initialize();
        setupUI();
    }

    private void initialize()
    {
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
        add(mainContent, BorderLayout.CENTER);
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

        ImageViewerLinkBuilder linkBuilder = new ImageViewerLinkBuilder();
        imageList = new ImageViewerImageList(this, linkBuilder);
        JScrollPane imageListScrollpane = new JScrollPane();
        imageListScrollpane.setViewportView(imageList);
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
}
