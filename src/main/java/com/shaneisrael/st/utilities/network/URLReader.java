package com.shaneisrael.st.utilities.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class URLReader
{
    static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/34.0.1847.116 " +
        "Safari/537.36 ";

    /**
     * Download content from a URL. Note that this is a blocking operation and should probably be done in the background.
     * 
     * @param urlString
     *            the URL to download from
     * @param userAgent
     *            the user agent to use
     * @return a string representation of the server's response
     * @throws IOException
     *             if there is an issue connecting to the server
     */
    String downloadString(String urlString, String userAgent) throws IOException
    {
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", userAgent);

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
        {
            buffer.append(line);
        }

        if (reader != null)
        {
            reader.close();
        }

        return buffer.toString();
    }

    /**
     * Download content from a URL. Note that this is a blocking operation and should probably be done in the background.
     * 
     * Uses a default User-Agent appearing to be Mozilla, WebKit, Chrome, and Safar.
     * 
     * @param urlString
     *            the URL to download from
     * @return a string representation of the server's response
     * @throws IOException
     *             if there is an issue connecting to the server
     */
    String downloadString(String urlString) throws IOException
    {
        return downloadString(urlString, DEFAULT_USER_AGENT);
    }
}
