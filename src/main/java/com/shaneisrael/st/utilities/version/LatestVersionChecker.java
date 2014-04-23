package com.shaneisrael.st.utilities.version;

/**
 * Checks the website for information about the latest version.
 * 
 * @author Talon
 * 
 */
public class LatestVersionChecker implements VersionProvider
{
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
    }

    @Override
    public Version fetchLatestVersion()
    {
        return null;
    }

}
