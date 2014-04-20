package com.shaneisrael.st.utilities;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import java.awt.GridLayout;

public class MultiUploader extends JFrame
{

    private JPanel contentPane;
    private Upload upload;
    private JTextArea linkBox;
    private JTextArea pathBox;
    private MultiUploaderThread mut;
    private JButton btnUpload;

    private int total_uploads = 0;
    private int current_upload = 0;
    private JTextArea deletionBox;

    /**
     * Create the frame.
     */
    public MultiUploader()
    {
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
            @Override
            public synchronized void drop(DropTargetDropEvent evt)
            {
                try
                {
                    evt.acceptDrop(DnDConstants.ACTION_LINK);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(
                            DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles)
                    {
                        /*
                         * NOTE: When I change this to a println, it prints the
                         * correct path
                         */
                        pathBox.setText(file.getPath() + "\n" + pathBox.getText());

                    }
                } catch (Exception ex)
                {
                    // JOptionPane.showMessageDialog(null,
                    // "Only image files can be uploaded.","Invalid File!",JOptionPane.WARNING_MESSAGE);
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
                // upload each image and get the return link and add it to the
                // link box.
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
                        links += link + "\n";
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
        total_uploads = pathBox.getText().split("\\n").length;

        for (String path : pathBox.getText().split("\\n"))
        {
            file = new File(path);
            String[] type = file.getPath().split("\\.");
            mut = new MultiUploaderThread(file, type[type.length - 1], this);
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
        current_upload++;
        linkBox.setText(link + "\n" + linkBox.getText());

        if (current_upload == total_uploads)
        {
            enableUploadButton();
        }
    }

    public void addDeletionLink(String link)
    {
        deletionBox.setText(link + "\n" + deletionBox.getText());
    }
}
