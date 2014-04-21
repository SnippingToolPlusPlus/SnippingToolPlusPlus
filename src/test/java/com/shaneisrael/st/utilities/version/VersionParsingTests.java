package com.shaneisrael.st.utilities.version;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.shaneisrael.st.utilities.version.Version;

public class VersionParsingTests
{
    @Test
    public void testParseValidVersionString1()
    {
        String versionString = "1.2.3";

        Version version = Version.fromString(versionString);
        assertEquals(1, version.getMajorVersion());
        assertEquals(2, version.getMinorVersion());
        assertEquals(3, version.getPatchVersion());
    }

    @Test
    public void testParseValidVersionString2()
    {
        String versionString = "017.2.001";

        Version version = Version.fromString(versionString);
        assertEquals(17, version.getMajorVersion());
        assertEquals(2, version.getMinorVersion());
        assertEquals(1, version.getPatchVersion());
    }

    @Test
    public void testParseValidVersionStringWithWhitespace()
    {
        String versionString = "  78.  3.2\t";
        Version version = Version.fromString(versionString);
        assertEquals(78, version.getMajorVersion());
        assertEquals(3, version.getMinorVersion());
        assertEquals(2, version.getPatchVersion());
    }

    @Test
    public void testParseValidVersionStringWithZeroes()
    {
        String versionString = "  0.0  .0\t";
        Version version = Version.fromString(versionString);
        assertEquals(0, version.getMajorVersion());
        assertEquals(0, version.getMinorVersion());
        assertEquals(0, version.getPatchVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNullVersionString()
    {
        String versionString = null;
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseGibberishVersionString()
    {
        String versionString = "a9s.dfg8.ha";
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseAlmostVersionString()
    {
        String versionString = "1.1.1.1";
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNegativeVersionString()
    {
        String versionString = "-1.1.1";
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseShortVersionString()
    {
        String versionString = "1.1";
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseShortVersionString2()
    {
        String versionString = "1";
        Version version = Version.fromString(versionString);
        assertEquals(null, version);
    }
}
