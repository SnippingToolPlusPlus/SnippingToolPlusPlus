package com.shaneisrael.st.utilities.network;

public interface ServerResponseListener
{

    /**
     * Called when receiving string content from a URL is successful.
     * 
     * @param content
     *            the content the server responded with.
     */
    public void onServerResponseSuccess(String content);

    /**
     * Called when receiving string content from a URL is not successful.
     * 
     * @param reason
     *            the reason it failed.
     */
    public void onServerResponseFail(String reason);
}
