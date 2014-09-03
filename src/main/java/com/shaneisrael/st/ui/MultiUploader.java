package com.shaneisrael.st.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.shaneisrael.st.imgur.ImgurImage;
import com.shaneisrael.st.imgur.ImgurResponse;
import com.shaneisrael.st.imgur.ImgurResponseListener;
import com.shaneisrael.st.imgur.ImgurUploader;
import com.shaneisrael.st.utilities.ClipboardUtilities;

public class MultiUploader extends JFrame implements ImgurResponseListener
{
    private static final long serialVersionUID = 5181546503719168014L;
    private final JPanel contentPane;
    private final JTextArea linkBox;
    private final JTextArea pathBox;
    private final JButton btnUpload;

    private final JTextArea deletionBox;
    private final ImgurUploader uploader;
    private int totalUploads = 0;
    private int currentUpload = 0;

    public MultiUploader()
    {
        uploader = new ImgurUploader();
        setTitle("Multi Image Uploader");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        ImageIcon ii2 = new ImageIcon(this.getClass().getResource("/images/icons/multi-image.png"));
        this.setIconImage(ii2.getImage());

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new MigLayout("", "[423.00,grow][-4.00]", "[169.00,center][25.00][114.00,grow][]"));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Drag images here"));
        panel.add(scrollPane, "cell 0 0,grow");

        pathBox = new JTextArea();
        pathBox.setEditable(false);
        scrollPane.setViewportView(pathBox);
        pathBox.setDropTarget(new DropTarget()
        {
            private static final long serialVersionUID = 9144941390359411732L;

            @SuppressWarnings("unchecked")
            @Override
            public synchronized void drop(DropTargetDropEvent event)
            {
                try
                {
                    event.acceptDrop(DnDConstants.ACTION_LINK);
                    Object transferData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (transferData instanceof List)
                    {
                        for (File file : (List<File>) transferData)
                        {
                            pathBox.append(file.getPath() + "\n");
                        }
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        JButton btnClearAll = new JButton("Clear all");
        btnClearAll.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                pathBox.setText("");
                deletionBox.setText("");
                linkBox.setText("");
            }
        });
        panel.add(btnClearAll, "flowx,cell 0 1,alignx right");

        btnUpload = new JButton("Upload");
        btnUpload.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!pathBox.getText().equals(""))
                {
                    uploadFiles();
                }
            }
        });
        panel.add(btnUpload, "cell 0 1,alignx right");

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, "cell 0 2,grow");
        panel_1.setLayout(new GridLayout(0, 2, 0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        panel_1.add(scrollPane_1);
        scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_1.setBorder(BorderFactory.createTitledBorder("Upload Links"));

        linkBox = new JTextArea();
        scrollPane_1.setViewportView(linkBox);
        linkBox.setEditable(false);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_2.setBorder(BorderFactory.createTitledBorder("Deletion Links"));
        panel_1.add(scrollPane_2);

        deletionBox = new JTextArea();
        deletionBox.setEditable(false);
        scrollPane_2.setViewportView(deletionBox);

        JButton btnCopyallTo = new JButton("Copy links to clipboard");
        btnCopyallTo.addActionListener(new ActionListener()
        {
            String links = "";

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (String link : linkBox.getText().split("\\n"))
                {
                    if (link.contains("imgur"))
                    {
                        links += link + "\n";
                    }
                }
                ClipboardUtilities.setClipboard(links);
            }
        });
        panel.add(btnCopyallTo, "cell 0 3");

        setVisible(true);
    }

    private void uploadFiles()
    {
        File file;
        disableUploadButton();
        totalUploads = pathBox.getText().split("\\n").length;

        for (String path : pathBox.getText().split("\\n"))
        {
            file = new File(path);
            uploader.upload(file, this);
        }
    }

    public void disableUploadButton()
    {
        btnUpload.setEnabled(false);
        pathBox.setEnabled(false);
        btnUpload.setText("Uploading...");
    }

    public void enableUploadButton()
    {
        btnUpload.setEnabled(true);
        pathBox.setEnabled(true);
        btnUpload.setText("Upload");
    }

    public void addLink(String link)
    {
        currentUpload++;
        linkBox.append(link + "\n");

        if (currentUpload == totalUploads)
        {
            enableUploadButton();
        }
    }

    public void addDeletionLink(String link)
    {
        deletionBox.append(link + "\n");
    }

    @Override
    public void onImgurResponseSuccess(ImgurImage uploadedImage)
    {
        addLink(uploadedImage.getLink());
        addDeletionLink(uploadedImage.getDeleteLink());
    }

    @Override
    public void onImgurResponseFail(ImgurResponse response)
    {
        addLink("Did not successfully upload to imgur." + response.getHttpStatusCode());
    }
}
