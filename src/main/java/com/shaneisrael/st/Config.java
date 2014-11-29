package com.shaneisrael.st;

import com.shaneisrael.st.utilities.version.Version;

public class Config
{
    //network
    public static final String WEBSITE_URL = "http://snippingtoolpluspl.us";
    public static final String DONATE_URL = "https://www.paypal.com/us/cgi-bin/webscr?cmd=_"
        + "flow&SESSION=K76DJYjsYQTyobfFvIBvyDm5q-Kq2ONOgHUQTJeEG0qQiTQx_OdRuFWgnpW&dispatch="
        + "5885d80a13c0db1f8e263663d3faee8d66f31424b43e9a70645c907a6cbd8fb4";
    
    public static final String STPP_USER_AGENT = "Snipping Tool++ v"
        + Version.getCurrentRunningVersion().getVersionString();

    //ui
    public static final int TRAY_ICON_NUM_FRAMES = 14;
    public static final int TRAY_ICON_FRAME_DELAY_MS = 38;

    //icons
    public static final String TRAY_ICON_BASE_DIR = "/images/upload/";
    public static final String TRAY_ICON_STATIC = "/images/trayIcon.png";

}
