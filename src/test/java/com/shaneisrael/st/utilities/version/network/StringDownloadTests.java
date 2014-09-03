package com.shaneisrael.st.utilities.version.network;

import static org.junit.Assert.*;

import org.junit.Test;

import com.shaneisrael.st.utilities.network.StringDownloader;

public class StringDownloadTests
{

    @Test
    public void testCanDownloadString()
    {
        String downloaded = StringDownloader.downloadString("http://nolat.org/tests/string");
        assertEquals("a string", downloaded);
    }
}
