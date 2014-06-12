package com.shaneisrael.st.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PreferencesTests
{
    //these tests will likely fail on non-Windows computers because the File class in java will insert platform specific things automatically
    @Test
    public void testWindowsDefaultDataPath()
    {
        String fakeRoot = "C:";
        Locations locations = new Locations(fakeRoot, OperatingSystem.WINDOWS);
        assertEquals("C:\\.snippingtool++\\data", locations.getDataDirectory().getAbsolutePath());
    }

    @Test
    public void testWindowsDefaultPreferencePath()
    {
        String fakeRoot = "C:";
        Locations locations = new Locations(fakeRoot, OperatingSystem.WINDOWS);
        assertEquals("C:\\.snippingtool++\\data\\prefs.json", locations.getPreferencesFile()
            .getAbsolutePath());
    }

    // These don't quite work because using the new File(parent, child); constructor will automatically convert all slashes and root information to the correct OS conventions.
    //    @Test
    //    public void testMacDefaultDataPath()
    //    {
    //        String fakeRoot = "/";
    //        Locations locations = new Locations(fakeRoot, OperatingSystem.MAC);
    //        assertEquals("/Users/test/Library/Application Support/snippingtool++/data/version5",
    //            locations.getDataDirectory().getAbsolutePath());
    //    }
    //
    //    @Test
    //    public void testMacDefaultPreferencePath()
    //    {
    //        String fakeRoot = "/Users/test";
    //        Locations locations = new Locations(fakeRoot, OperatingSystem.MAC);
    //        assertEquals("/Users/test/Library/Application Support/snippingtool++/data/version5/prefs.json",
    //            locations.getPreferencesFile().getAbsolutePath());
    //    }
}
