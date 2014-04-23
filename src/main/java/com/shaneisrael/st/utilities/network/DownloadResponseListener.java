package com.shaneisrael.st.utilities.network;

public interface DownloadResponseListener
{

    /**
     * Called when downloading string content from a URL is successful.
     * 
     * @param content
     *            the content the server responded with.
     */
    public void onSuccess(String content);

    /**
     * Called when downloading string content from a URL is not successful.
     * 
     * @param reason
     *            the reason it failed.
     */
    public void onFailure(String reason);
}
