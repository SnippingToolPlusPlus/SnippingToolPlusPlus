package com.shaneisrael.st.utilities.version;

public interface VersionProvider
{
    /**
     * Retrieves the latest version information and alerts the given {@link VersionResponseListener}
     * 
     * @param listener
     *            the listener to receive the latest version.
     */
    public void fetchLatestVersion(VersionResponseListener listener);
}
