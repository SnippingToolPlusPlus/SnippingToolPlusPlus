package com.shaneisrael.st.utilities.version;

public interface VersionResponseListener
{

    /**
     * Called when checking for the latest version is successful.
     * 
     * @param latestVersion
     */
    public void onVersionResponseSuccess(Version latestVersion);

    /**
     * Called when checking for the latest version is not successful.
     * 
     * @param reason
     *            the reason for the failure, should be end-user understandable.
     */
    public void onVersionResponseFailure(String reason);
}
