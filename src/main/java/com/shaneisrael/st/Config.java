package com.shaneisrael.st;

import com.shaneisrael.st.utilities.version.Version;

public class Config
{
    //network
    public static final String WEBSITE_URL = "http://snippingtoolpluspl.us";
    public static final String DONATE_URL = "http://snippingtoolpluspl.us/donate/";
    
    public static final String STPP_USER_AGENT = "Snipping Tool++ v"
        + Version.getCurrentRunningVersion().getVersionString();

    //ui
    public static final int TRAY_ICON_NUM_FRAMES = 14;
    public static final int TRAY_ICON_FRAME_DELAY_MS = 38;

    //icons
    public static final String TRAY_ICON_BASE_DIR = "/images/upload/";
    public static final String TRAY_ICON_STATIC = "/images/trayIcon.png";
    
    //identifiers
    public static final int UPLOAD_SNIPPET_ID = 0;
    public static final int UPLOAD_SCREEN_ID = 1;
    public static final int SAVE_SNIPPET_ID = 2;
    public static final int SAVE_SCREEN_ID = 3;
    public static final int UPLOAD_CLIPBOARD_ID = 4;
    public static final int FTP_UPLOAD_SNIPPET_ID = 5;
    public static final int FTP_UPLOAD_SCREEN_ID = 6;

}
