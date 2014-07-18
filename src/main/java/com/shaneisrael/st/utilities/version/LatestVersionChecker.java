package com.shaneisrael.st.utilities.version;

import com.shaneisrael.st.utilities.network.ServerResponseListener;
import com.shaneisrael.st.utilities.network.StringDownloader;

/**
 * Checks the server for information about the latest version.
 * 
 * @author Talon
 * 
 */
public class LatestVersionChecker implements VersionProvider, ServerResponseListener
{
    private static final String VERSION_URL = "http://snippingtoolpluspl.us/version.json?"
        + Version.getCurrentRunningVersion().getVersionString();
    private VersionResponseListener responder;

    @Override
    public void onServerResponseSuccess(String versionJson)
    {
        responder.onVersionResponseSuccess(Version.fromJson(versionJson));
    }

    @Override
    public void onServerResponseFail(String reason)
    {
        responder.onVersionResponseFailure(reason);
    }

    @Override
    public void fetchLatestVersion(VersionResponseListener listener)
    {
        this.responder = listener;
        StringDownloader downloader = new StringDownloader();
        downloader.downloadAsync(VERSION_URL, this);
    }
}
