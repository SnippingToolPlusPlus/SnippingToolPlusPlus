package com.shaneisrael.st.ui.imageviewer;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

public class ImageViewerImageList extends JList<ImageLinkPair>
{
    private static final long serialVersionUID = -3708773009407404197L;
    private final ImageViewerLinkBuilder linkBuilder;

    public ImageViewerImageList(ListSelectionListener listSelectionListener, ImageViewerLinkBuilder linkBuilder)
    {
        this.linkBuilder = linkBuilder;

        this.addListSelectionListener(listSelectionListener);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setListModel();
    }

    private void setListModel()
    {
        final ArrayList<ImageLinkPair> items = linkBuilder.getImages();
        this.setModel(new AbstractListModel<ImageLinkPair>()
        {
            private static final long serialVersionUID = 2780686149749642099L;

            @Override
            public ImageLinkPair getElementAt(int index)
            {
                return items.get(index);
            }

            @Override
            public int getSize()
            {
                return items.size();
            }

        });
    }
}
