package com.shaneisrael.st;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;
import com.melloware.jintellitype.JIntellitypeException;
import com.shaneisrael.st.data.Locations;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.editor.Editor;
import com.shaneisrael.st.notification.STNotification;
import com.shaneisrael.st.notification.STNotificationQueue;
import com.shaneisrael.st.notification.STNotificationType;
import com.shaneisrael.st.notification.STTheme;
import com.shaneisrael.st.overlay.Overlay;
import com.shaneisrael.st.overlay.OverlayFrame;
import com.shaneisrael.st.prefs.Preferences;
import com.shaneisrael.st.prefs.Hotkeys.Hotkeys;
import com.shaneisrael.st.ui.AboutFrame;
import com.shaneisrael.st.ui.MultiUploader;
import com.shaneisrael.st.ui.PreferencesUI;
import com.shaneisrael.st.ui.imageviewer.ImageViewer;
import com.shaneisrael.st.upload.SimpleFTPUploader;
import com.shaneisrael.st.utilities.Browser;
import com.shaneisrael.st.utilities.CaptureScreen;
import com.shaneisrael.st.utilities.ClipboardUtilities;
import com.shaneisrael.st.utilities.ImageUtilities;
import com.shaneisrael.st.utilities.Save;
import com.shaneisrael.st.utilities.Upload;
import com.shaneisrael.st.utilities.version.UpdateChecker;
import com.shaneisrael.st.utilities.version.Version;

public class SnippingToolPlusPlus extends JFrame implements ActionListener, JIntellitypeConstants
{
    public static boolean debugging = false;
    public static JXTrayIcon trayIcon;

    private OverlayFrame overlay;
    private PreferencesUI preferencesUI;
    private Save save;
    private Upload upload;

    private JMenuItem uScreenshot;
    private JMenuItem uSnippet;
    private JMenuItem sScreenshot;
    private JMenuItem sSnippet;
    private JMenuItem uClipboardImg;
    private CaptureScreen capture = new CaptureScreen();
    private static STNotificationQueue notificationQueue;
    private static STNotification notification;

    private UpdateChecker updater;
    final String iconMac = "trayIconMac.png";
    final String iconPressedMac = "trayIconPressedMac.png";

    public static void main(String... args)
    {
        processArguments(args);
        SnippingToolPlusPlus frame = null;
        try
        {
            frame = new SnippingToolPlusPlus();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (HeadlessException ex)
        {
            System.out.println("SnippingTool++ was not designed to take screenshots of terminals.");
            System.out.println("Feel free to implement the feature and submit a patch though!");
            System.out.println("Source: https://github.com/SnippingToolPlusPlus/SnippingToolPlusPlus");
        }
    }

    private static void processArguments(String[] args)
    {
        if (args.length > 0)
        {
            System.out.println("Arguments: " + Arrays.deepToString(args));
            if (args[0].equals("-d"))
            {
                debugging = true;
            }

        }
    }

    public SnippingToolPlusPlus()
    {
        Preferences.getInstance().refresh();
        Preferences.getInstance().checkDirectories();
        System.out.println(Preferences.getInstance().getCaptureDirectoryRoot());
        System.out.println("Version: " + Version.getCurrentRunningVersion());
        System.out.println("Locations: " + new Locations().toString());
        setUndecorated(true);
        setAlwaysOnTop(true);

        try
        {
            if (OperatingSystem.isWindows())
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
            }

        } catch (Exception e)
        {
        }

        initializeNotifications();

        initializeTray();

        /*
         * Hotkeys will function here. This is the main class, it will hold the
         * tray icon and be the basic control class. It will initialize the
         * overlay when the specified hotkeys are pressed.
         * 
         * Hotkeys will not work on Mac
         */
        if (OperatingSystem.isWindows())
        {
            initializeHotkeys();
        } else
        {
            System.out.println(OperatingSystem.getCurrentOS() + " does not support global hotkeys.");
        }

        updater = new UpdateChecker();
        updater.checkForUpdates();
        
    }

    private void initializeNotifications()
    {
        STTheme.setThemePath("/theme/cloudy");
        notificationQueue = new STNotificationQueue(17);
    }

    private void initializeHotkeys()
    {
        /*
         * register hotkeys
         */
        JIntellitype keyhook = null;
        Hotkeys hotkeys;
        try
        {
            keyhook = JIntellitype.getInstance();
        } catch (JIntellitypeException ex)
        {
            JOptionPane.showMessageDialog(null, "Please only run one copy of Snipping Tool++ at once");
            System.exit(1);
        }
        if (keyhook != null)
        {
            hotkeys = new Hotkeys();
            hotkeys.registerHotkeys();
        }
        /*
         * events
         */

        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener()
        {

            @Override
            public void onHotKey(int identifier)
            {
                if (identifier == Config.UPLOAD_SNIPPET_ID) 
                {
                    uSnippet.doClick();
                } else if (identifier == Config.UPLOAD_SCREEN_ID) 
                {
                    uScreenshot.doClick();
                } else if (identifier == Config.SAVE_SNIPPET_ID) 
                {
                    sSnippet.doClick();
                } else if (identifier == Config.SAVE_SCREEN_ID)
                {
                    sScreenshot.doClick();
                } else if (identifier == Config.UPLOAD_CLIPBOARD_ID)
                {
                    uClipboardImg.doClick();
                } else if (identifier == Config.FTP_UPLOAD_SNIPPET_ID)
                {
                    displayOverlay();
                    overlay.setMode(Overlay.UPLOAD_FTP);
                } else if (identifier == Config.FTP_UPLOAD_SCREEN_ID)
                {
                    System.out.println("test!");
                    new SimpleFTPUploader(ImageUtilities.saveTemporarily(capture.getScreenCapture()));
                }

            }
        });

    }

    private void displayOverlay()
    {
        if (overlay == null || OverlayFrame.IsActive == false)
        {
            if(Editor.getInstance().isActive())
                Editor.getInstance().dispose();
            
            overlay = new OverlayFrame();
        }
    }

    public static void showNotification(String title, STNotificationType type)
    {
        notification = new STNotification(title, type);
        notification.setPauseTime(1500);
        notificationQueue.add(notification);
    }

    public static void showNotification(String title, STNotificationType type, int pauseTime)
    {
        notification = new STNotification(title, type);
        notification.setPauseTime(pauseTime);
        notificationQueue.add(notification);
    }

    public static void showNotification(STNotification notification)
    {
        notificationQueue.add(notification);
    }

    private void initializeTray()
    {
        final String icon = "trayIcon.png";

        System.out.println("Running " + OperatingSystem.getCurrentOS());

        if (!OperatingSystem.isAny(OperatingSystem.WINDOWS, OperatingSystem.MAC))
        {
            /*
             * Because it can only run on windows (maybe osx) currently, we kill the
             * program if it detects non-windows or non osx before going further.
             */
            if (!debugging)
            {
                System.out.println("This OS is not yet supported. Exiting.");
                System.out.println("You can override this by running the application with -d as the first argument.");
                System.exit(0);
            }
        }

        ImageIcon ii = new ImageIcon(this.getClass().getResource(
            "/images/" + (OperatingSystem.isWindows() ? icon : iconMac)));

        final JPopupMenu popup = new JPopupMenu();

        trayIcon = new JXTrayIcon(ii.getImage());
        trayIcon.addActionListener(this);
        trayIcon.setActionCommand("tray");

        final SystemTray tray = SystemTray.getSystemTray();

        JMenu utilitiesMenu = new JMenu("Utilities");
        utilitiesMenu.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/utilities.png")));
        JMenuItem imageViewer = new JMenuItem("Capture Viewer");
        imageViewer.addActionListener(this);
        imageViewer.setActionCommand("viewer");
        imageViewer.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/folder.png")));

        JMenu uploadMenu = new JMenu("Upload");
        uploadMenu.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/image_upload.png")));
        JMenu saveMenu = new JMenu("Save");
        saveMenu.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/image_save.png")));
        JMenuItem prefMenu = new JMenuItem("Preferences");
        prefMenu.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/pref.png")));
        prefMenu.addActionListener(this);
        prefMenu.setActionCommand("preferences");
        uScreenshot = new JMenuItem("Screenshot ["+Hotkeys.getHotkeyComboText(Config.UPLOAD_SCREEN_ID)+"]");
        uScreenshot.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/screenshot.png")));
        uScreenshot.addActionListener(this);
        uScreenshot.setActionCommand("uScreen");
        uSnippet = new JMenuItem("Snippet ["+Hotkeys.getHotkeyComboText(Config.UPLOAD_SNIPPET_ID)+"]");
        uSnippet.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/snippet.png")));
        uSnippet.addActionListener(this);
        uSnippet.setActionCommand("uSnippet");

        uClipboardImg = new JMenuItem("Clipboard Image ["+Hotkeys.getHotkeyComboText(Config.UPLOAD_CLIPBOARD_ID)+"]");
        uClipboardImg.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/image_upload.png")));
        uClipboardImg.addActionListener(this);
        uClipboardImg.setActionCommand("uClipboardImg");

        sScreenshot = new JMenuItem("Screenshot ["+Hotkeys.getHotkeyComboText(Config.SAVE_SCREEN_ID)+"]");
        sScreenshot.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/screenshot.png")));
        sScreenshot.addActionListener(this);
        sScreenshot.setActionCommand("sScreen");
        sSnippet = new JMenuItem("Snippet ["+Hotkeys.getHotkeyComboText(Config.SAVE_SNIPPET_ID)+"]");
        sSnippet.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/snippet.png")));
        sSnippet.addActionListener(this);
        sSnippet.setActionCommand("sSnippet");
        JMenuItem multiUploadItem = new JMenuItem("Multi Image Uploader");
        multiUploadItem.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/multi-image.png")));
        multiUploadItem.addActionListener(this);
        multiUploadItem.setActionCommand("multi_upload");
        JMenuItem exitItem = new JMenuItem("Quit");
        exitItem.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/exit.png")));
        exitItem.addActionListener(this);
        exitItem.setActionCommand("exit");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/about_icon.png")));
        aboutItem.addActionListener(this);
        aboutItem.setActionCommand("about");
        JMenuItem donateItem = new JMenuItem("Donate...");
        donateItem.setIcon(new ImageIcon(this.getClass().getResource("/images/icons/heart-icon.png")));
        donateItem.addActionListener(this);
        donateItem.setActionCommand("donate");
        uploadMenu.add(uSnippet);
        uploadMenu.add(uScreenshot);
        uploadMenu.add(uClipboardImg);
        saveMenu.add(sSnippet);
        saveMenu.addSeparator();
        saveMenu.add(sScreenshot);

        utilitiesMenu.add(multiUploadItem);
        utilitiesMenu.add(imageViewer);

        popup.add(aboutItem);
        popup.add(donateItem);
        popup.add(prefMenu);
        popup.addSeparator();
        popup.add(utilitiesMenu);
        popup.addSeparator();
        popup.add(uploadMenu);
        popup.add(saveMenu);
        popup.addSeparator();
        popup.add(exitItem);

        popup.setLightWeightPopupEnabled(true);
        trayIcon.setJPopupMenu(popup);

        final Frame frame = new Frame("");
        frame.setUndecorated(true);

        try
        {
            tray.add(trayIcon);
            showNotification("welcome", STNotificationType.SUCCESS, 3000);
        } catch (AWTException e)
        {
            System.out.println("TrayIcon could not be added.");
        }

        if (!OperatingSystem.isWindows())
        {
            trayIcon.addMouseListener(new MouseListener()
            {

                @Override
                public void mousePressed(MouseEvent e)
                {
                    trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/" + iconPressedMac))
                        .getImage());
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        JOptionPane.showMessageDialog(null, "Pointer Location: OOOh a message");
                        frame.setVisible(true);
                        frame.setLocation(e.getLocationOnScreen().x, e.getLocationOnScreen().y);

                        frame.add(popup);
                        popup.show(frame, e.getXOnScreen(), 0);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/" + iconMac)).getImage());
                    frame.setVisible(false);
                }

                // gotta have these although i don't need them
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    trayIcon.setImage(new ImageIcon(this.getClass().getResource("/images/" + iconPressedMac))
                        .getImage());
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        frame.setVisible(true);
                        frame.setLocationRelativeTo(frame);
                        frame.add(popup);
                        popup.show(frame, e.getXOnScreen(), 0);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e)
                {
                }

                @Override
                public void mouseExited(MouseEvent e)
                {
                }
            });
            frame.setResizable(false);
            frame.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object command = e.getActionCommand();
        if (command.equals("uSnippet"))
        {
            displayOverlay();
            overlay.setMode(Overlay.UPLOAD);
        } else if (command.equals("uScreen"))
        {
            upload = new Upload(capture.getScreenCapture(), false);
        } else if (command.equals("sSnippet"))
        {
            displayOverlay();
            overlay.setMode(Overlay.SAVE);
        } else if (command.equals("sScreen"))
        {
            save = new Save();
            save.save(capture.getScreenCapture());
        } else if (command.equals("uClipboardImg"))
        {
            ClipboardUtilities.uploadImage();
        } else if (command.equals("about"))
        {
            new AboutFrame();
        } else if (command.equals("viewer"))
        {
            new ImageViewer();
        } else if (command.equals("preferences"))
        {
            preferencesUI = new PreferencesUI();
        } else if (command.equals("multi_upload"))
        {
            new MultiUploader();
        } else if (command.equals("tray"))
        {
            if (OperatingSystem.isWindows())
            {
                try
                {
                    Desktop.getDesktop().open(new File(Preferences.getInstance().getCaptureDirectoryRoot()));
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        else if(command.equals("donate"))
        {
            Browser.open(Config.DONATE_URL);
        }
        else if (command.equals("exit"))
        {
            JIntellitype.getInstance().cleanUp();
            System.exit(0);
        }
    }
}
