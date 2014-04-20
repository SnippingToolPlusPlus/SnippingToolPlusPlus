package com.shaneisrael.st.utilities;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenBrowser
{
    private static final String REDDIT_SUBMIT_LINK = "www.reddit.com/submit?url=";

    public static void open(URI link)
    {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
        {
            try
            {
                desktop.browse(link);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void openToReddit(String link)
    {
        URI url = null;
        try
        {
            url = new URI(REDDIT_SUBMIT_LINK + link);
        } catch (URISyntaxException e1)
        {
            e1.printStackTrace();
        }
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
        {
            try
            {
                if (url != null)
                {
                    desktop.browse(url);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
