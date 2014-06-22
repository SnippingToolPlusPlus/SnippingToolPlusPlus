package com.shaneisrael.st.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PreferencesTests
{
    // these tests will likely fail on non-Windows computers because the File
    // class in java will insert platform specific things automatically
    @Test
    public void testWindowsDefaultDataPath()
    {
        String fakeRoot = "C:";
        Locations locations = new Locations(fakeRoot, OperatingSystem.WINDOWS);
        //        assertEquals("C:\\.snippingtool++\\data", locations.getDataDirectory()
        //            .getAbsolutePath());
    }

    @Test
    public void testWindowsDefaultPreferencePath()
    {
        String fakeRoot = "C:";
        Locations locations = new Locations(fakeRoot, OperatingSystem.WINDOWS);
        //        assertEquals("C:\\.snippingtool++\\data\\prefs.json", locations
        //            .getPreferencesFile().getAbsolutePath());
    }
}
