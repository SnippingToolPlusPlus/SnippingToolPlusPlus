package com.shaneisrael.st.utilities.version.network;

import static org.junit.Assert.*;

import org.junit.Test;

import com.shaneisrael.st.utilities.network.URLDownloader;

public class StringDownloadTests
{

    @Test
    public void testCanDownloadString()
    {
        String downloaded = URLDownloader.downloadString("http://nolat.org/tests/string");
        assertEquals("a string", downloaded);
    }
}
