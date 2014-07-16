package com.shaneisrael.st.utilities.version;

import com.shaneisrael.st.utilities.network.DownloadResponseListener;
import com.shaneisrael.st.utilities.network.URLDownloader;

/**
 * Checks the server for information about the latest version.
 * 
 * @author Talon
 * 
 */
public class LatestVersionChecker implements VersionProvider, DownloadResponseListener
{
    private static final String VERSION_URL = "http://snippingtoolpluspl.us/version.json?"
        + Version.getCurrentRunningVersion().getVersionString();
    private VersionResponseListener responder;

    @Override
    public void onSuccess(String versionJson)
    {
        responder.onVersionResponseSuccess(Version.fromJson(versionJson));
    }

    @Override
    public void onFailure(String reason)
    {
        responder.onVersionResponseFailure(reason);
    }

    @Override
    public void fetchLatestVersion(VersionResponseListener listener)
    {
        this.responder = listener;
        URLDownloader downloader = new URLDownloader();
        downloader.downloadAsync(VERSION_URL, this);
    }
}
