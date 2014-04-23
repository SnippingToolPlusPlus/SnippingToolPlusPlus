package com.shaneisrael.st.utilities.version;

import com.shaneisrael.st.utilities.network.DownloadResponseListener;
import com.shaneisrael.st.utilities.network.URLDownloader;

/**
 * Checks the website for information about the latest version.
 * 
 * @author Talon
 * 
 */
public class LatestVersionChecker implements DownloadResponseListener
{
    private static final String VERSION_URL = "http://snippingtoolpluspl.us/version.json";
    private VersionResponseListener responder;

    /**
     * Creates an instance of {@link LatestVersionChecker} that will run in a background thread.
     * 
     * When the server sends a response (or an error occurs), the responder will be notified.
     * 
     * @param responder
     */
    public LatestVersionChecker(VersionResponseListener responder)
    {
        this.responder = responder;
        URLDownloader downloader = new URLDownloader();
        downloader.downloadAsync(VERSION_URL, this);
    }

//    public Version fetchLatestVersion()
//    {
//        return null;
//    }

    @Override
    public void onSuccess(String versionJson)
    {
        responder.onSuccess(Version.fromJson(versionJson));
    }

    @Override
    public void onFailure(String reason)
    {
        responder.onFailure(reason);
    }

}
