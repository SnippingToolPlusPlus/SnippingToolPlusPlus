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
        String expected = "1.2.3";

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
            assertEquals("5.3.1 [5.3.5 - Fuzzy Foo]", version.toString());
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
            assertEquals(versionWithEverythingOutput.trim(), version.toString().trim().replaceAll("\r", ""));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static final String versionWithEverything = "{\n" +
        "    \"version_name\": \"5.3.5 - Fuzzy Foo\",\n" +
        "    \"major\": \"5\",\n" +
        "    \"minor\": \"3\",\n" +
        "    \"patch\": \"5\",\n" +
        "    \"download\": \"http://snippingtoolpluspl.us/download/Snipping Tool++ (v5.3.5).jar\",\n" +
        "    \"changes\": {\n" +
        "        \"added\": [\n" +
        "            \"this is a list\", \"of additions\", \"for this patch\"\n" +
        "        ],\n" +
        "        \"removed\": [\n" +
        "            \"this is a list\", \"of things that\", \"were removed\"\n" +
        "        ],\n" +
        "        \"changed\": [\n" +
        "            \"a list of things\", \"that have changed\"\n" +
        "        ],\n" +
        "        \"fixed\": [\n" +
        "            \"all the bugs\", \"that don't exist now\"\n" +
        "        ]\n" +
        "    },\n" +
        "    \"note\": \"Just some extra information from the developer\"\n" +
        "}";

    private static final String versionWithEverythingOutput = "5.3.5 [5.3.5 - Fuzzy Foo]\n" +
        "\n" +
        "Added:\n" +
        "+ this is a list\n" +
        "+ of additions\n" +
        "+ for this patch\n" +
        "\n" +
        "Removed:\n" +
        "- this is a list\n" +
        "- of things that\n" +
        "- were removed\n" +
        "\n" +
        "Changed:\n" +
        "* a list of things\n" +
        "* that have changed\n" +
        "\n" +
        "Bug Fixes:\n" +
        "# all the bugs\n" +
        "# that don't exist now\n" +
        "\n" +
        "\n" +
        "Note: Just some extra information from the developer\n" +
        "Download: http://snippingtoolpluspl.us/download/Snipping Tool++ (v5.3.5).jar";

    private static final String versionWithName = "{ \"version_name\": \"5.3.5 - Fuzzy Foo\",\n" +
        "    \"major\": \"5\",\n" +
        "    \"minor\": \"3\",\n" +
        "    \"patch\": \"1\"\n" +
        "}";

}
