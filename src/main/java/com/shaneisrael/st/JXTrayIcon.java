package com.shaneisrael.st;

/*
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * This particular file is subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Based on a blog post from Alexander Potochkin at the following url:
 * http://weblogs.java.net/blog/alexfromsun/archive/2008/02/jtrayicon_updat.html
 * 
 * @author Alexander Potochkin
 * @author Stephen Chin
 * @author Keith Combs
 */
public class JXTrayIcon extends TrayIcon
{
	private JPopupMenu menu;
	private static JDialog dialog;
	static
	{
		dialog = new JDialog((Frame) null, "TrayDialog");
		dialog.setUndecorated(true);
		dialog.setAlwaysOnTop(true);
	}

	private static PopupMenuListener popupListener = new PopupMenuListener()
	{
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e)
		{
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
		{
			dialog.setVisible(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e)
		{
			dialog.setVisible(false);
		}
	};

	public JXTrayIcon(Image image)
	{
		super(image);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showJPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showJPopupMenu(e);
			}
		});
	}

	public JXTrayIcon(Image image, String string)
	{
		super(image, string);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showJPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showJPopupMenu(e);
			}
		});
	}

	private void showJPopupMenu(MouseEvent e)
	{
		if (e.isPopupTrigger() && menu != null)
		{
			Dimension size = menu.getPreferredSize();
			int adjustedY = e.getY() - size.height;
			dialog.setLocation(e.getX(), adjustedY < 0 ? e.getY() : adjustedY);
			dialog.setVisible(true);
			menu.show(dialog.getContentPane(), 0, 0);
			// popup works only for focused windows
			dialog.toFront();
		}
	}

	public JPopupMenu getJPopupMenu()
	{
		return menu;
	}

	public void setJPopupMenu(JPopupMenu menu)
	{
		if (this.menu != null)
		{
			this.menu.removePopupMenuListener(popupListener);
		}
		this.menu = menu;
		menu.addPopupMenuListener(popupListener);
	}

}