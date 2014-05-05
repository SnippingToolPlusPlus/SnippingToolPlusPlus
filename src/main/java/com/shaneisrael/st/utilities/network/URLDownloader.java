package com.shaneisrael.st.utilities.network;

import java.io.IOException;

/**
 * Downloads a String from a URL in the background and alerts a listener when it finishes or an error occurs.
 * 
 * @author Talon
 * 
 */
public class URLDownloader implements Runnable, DownloadResponseListener
{
    private String url;
    private DownloadResponseListener listener;

    private String downloadedContent;;
    private String errorMessage;
    private boolean wasSuccessful;

    /**
     * Asynchronously performs the string download and calls the listener when finished.
     * 
     * @param url
     *            the url to download from
     * @param listener
     *            the listener to notify when finished
     */
    public void downloadAsync(String url, DownloadResponseListener listener)
    {
        this.url = url;
        this.listener = listener;
        new Thread(this, "[" + url + "] downloader").start();
    }

    /**
     * Performs a blocking download and returns the String it downloaded from the server, or an error message if it fails.
     * 
     * @param url
     *            the url to download from
     * @return
     */
    public String download(String url)
    {
        this.url = url;
        this.listener = this;
        run();
        return wasSuccessful ? downloadedContent : errorMessage;
    }

    @Override
    public void run()
    {
        String serverResponse = "";
        URLReader urlReader = new URLReader();
        try
        {
            serverResponse = urlReader.downloadString(url);
        } catch (IOException e)
        {
            listener.onFailure(e.getMessage());
        }
        listener.onSuccess(serverResponse);
    }

    @Override
    public void onSuccess(String content)
    {
        wasSuccessful = true;
        downloadedContent = content;
    }

    @Override
    public void onFailure(String reason)
    {
        wasSuccessful = false;
        errorMessage = reason;
    }

    /**
     * A convenience method for just downloading a string and blocking until it returns.
     * 
     * @param url
     *            the url to download from
     * @return the string that the server responds with at the url, null if there was an error.
     */
    public static String downloadString(String url)
    {
        return new URLDownloader().download(url);
    }
}
