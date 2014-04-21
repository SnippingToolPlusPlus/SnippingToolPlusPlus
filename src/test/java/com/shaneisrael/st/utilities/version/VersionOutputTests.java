package com.shaneisrael.st.utilities.version;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;

public class VersionOutputTests
{

    @Test
    public void testVersionJustString()
    {
        Version version = Version.fromString("1.2.3");
        String expected = "Version 1.2.3";

        assertEquals(expected, version.toString());
    }

    @Test
    public void testVersionWithName()
    {
        Gson gson = new Gson();
        try
        {
            Version version = gson.fromJson(versionWithName, Version.class);
            assertEquals(5, version.getMajorVersion());
            assertEquals(3, version.getMinorVersion());
            assertEquals(1, version.getPatchVersion());
            assertEquals("5.3.5 - Fuzzy Foo", version.getVersionName());
            assertEquals("Version 5.3.1 [5.3.5 - Fuzzy Foo]", version.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testVersionWithEverything()
    {
        Gson gson = new Gson();
        try
        {
            Version version = gson.fromJson(versionWithEverything, Version.class);
            System.out.println(version);
            assertEquals(versionWithEverythingOutput.trim(), version.toString().trim());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static final String versionWithEverything = "{\r\n" +
        "    \"version_name\": \"5.3.5 - Fuzzy Foo\",\r\n" +
        "    \"major\": \"5\",\r\n" +
        "    \"minor\": \"3\",\r\n" +
        "    \"patch\": \"5\",\r\n" +
        "    \"download\": \"http://snippingtoolpluspl.us/download/Snipping Tool++ (v5.3.5).jar\",\r\n" +
        "    \"changes\": {\r\n" +
        "        \"added\": [\r\n" +
        "            \"this is a list\", \"of additions\", \"for this patch\"\r\n" +
        "        ],\r\n" +
        "        \"removed\": [\r\n" +
        "            \"this is a list\", \"of things that\", \"were removed\"\r\n" +
        "        ],\r\n" +
        "        \"changed\": [\r\n" +
        "            \"a list of things\", \"that have changed\"\r\n" +
        "        ],\r\n" +
        "        \"fixed\": [\r\n" +
        "            \"all the bugs\", \"that don't exist now\"\r\n" +
        "        ]\r\n" +
        "    },\r\n" +
        "    \"note\": \"Just some extra information from the developer\"\r\n" +
        "}";

    private static final String versionWithEverythingOutput = "Version 5.3.5 [5.3.5 - Fuzzy Foo]\r\n" +
        "\r\n" +
        "Added:\r\n" +
        "+ this is a list\r\n" +
        "+ of additions\r\n" +
        "+ for this patch\r\n" +
        "\r\n" +
        "Removed:\r\n" +
        "- this is a list\r\n" +
        "- of things that\r\n" +
        "- were removed\r\n" +
        "\r\n" +
        "Changed:\r\n" +
        "* a list of things\r\n" +
        "* that have changed\r\n" +
        "\r\n" +
        "Bug Fixes:\r\n" +
        "# all the bugs\r\n" +
        "# that don't exist now\r\n" +
        "\r\n" +
        "\r\n" +
        "Note: Just some extra information from the developer\r\n" +
        "Download: http://snippingtoolpluspl.us/download/Snipping Tool++ (v5.3.5).jar";

    private static final String versionWithName = "{ \"version_name\": \"5.3.5 - Fuzzy Foo\",\r\n" +
        "    \"major\": \"5\",\r\n" +
        "    \"minor\": \"3\",\r\n" +
        "    \"patch\": \"1\"\r\n" +
        "}";

}
