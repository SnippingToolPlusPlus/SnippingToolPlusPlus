package com.shaneisrael.st.utilities.version;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.shaneisrael.st.data.Logger;
import com.shaneisrael.st.prefs.Preferences;

/**
 * Represents the Version information.
 * 
 * Loosely follows Semantic Versioning (http://semver.org/)
 * 
 * @author Talon
 * 
 */
public final class Version implements Comparable<Version>
{
    @SerializedName("version_name")
    private String versionName;

    @SerializedName("major")
    private int majorVersion;

    @SerializedName("minor")
    private int minorVersion;

    @SerializedName("patch")
    private int patchVersion;

    @SerializedName("download")
    private String downloadLocation;

    @SerializedName("changes")
    private ChangeLog changeLog;

    @SerializedName("note")
    private String note;

    /**
     * The human readable version name. This is what is displayed to the user.
     * 
     * @return the versionName
     */
    public String getVersionName()
    {
        return versionName;
    }

    /**
     * 
     * @return The major.minor.patch formatted version string
     */
    public String getVersionString()
    {
        return String.format("%d.%d.%d", getMajorVersion(), getMinorVersion(), getPatchVersion());
    }

    /**
     * 
     * @return The version string in major.mine.patch [version name] form
     */
    public String getVersionStringWithName()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getVersionString());

        if (getVersionName() != null && !getVersionName().isEmpty())
        {
            builder.append(" [" + getVersionName() + "]");
        }

        return builder.toString();
    }

    /**
     * Changed when the user will have to manually do something due to incompatibilities with previous versions.
     * 
     * @return the major version
     */
    public int getMajorVersion()
    {
        return majorVersion;
    }

    /**
     * Changed when new functionality is added.
     * 
     * @return the minor version
     */
    public int getMinorVersion()
    {
        return minorVersion;
    }

    /**
     * Changed when other small changes are made such as bug fixes (that are backwards compatible)
     * 
     * @return the patch version
     */
    public int getPatchVersion()
    {
        return patchVersion;
    }

    /**
     * @return A download link to the latest version of the application.
     */
    public String getDownloadLocation()
    {
        return downloadLocation;
    }

    /**
     * @return the changeLog
     */
    public ChangeLog getChangeLog()
    {
        return changeLog;
    }

    /**
     * @return An optional message that can be included with the version info.
     */
    public String getNote()
    {
        return note;
    }

    /**
     * Compares this version against another.
     * 
     * Uses the major/minor/patch numbers to determine a higher version.
     * 
     * First compares major versions, if they're the same, compare minor, if they're the same, compare patch.
     * 
     * If the other version is null, say this version is greater.
     * 
     * @param other
     *            other version to compare to
     * @return -1 if this version is lower, 0 if the same version, 1 if this version is higher
     */
    @Override
    public int compareTo(Version other)
    {
        if (other == null)
        {
            return 1;
        }

        if (majorVersion == other.majorVersion)
        {
            if (minorVersion == other.minorVersion)
            {
                if (patchVersion == other.patchVersion)
                {
                    return 0;
                } else
                {
                    return Integer.compare(patchVersion, other.patchVersion);
                }
            } else
            {
                return Integer.compare(minorVersion, other.minorVersion);
            }
        }
        return Integer.compare(majorVersion, other.majorVersion);
    }

    /**
     * Parses version information from a string. All other fields are blank.
     * 
     * @param version
     *            A version string in the form major.minor.patch where major/minor/patch are integers >= 0. Any whitespace will be removed before parsing.
     * @return A {@link Version} object representing the string version number, null if there were errors.
     * @throws IllegalArgumentException
     *             if the version string is not the correct format
     */
    public static Version fromString(String versionString)
    {
        if (versionString == null)
        {
            throw new IllegalArgumentException("versionString parameter cannot be null");
        }

        versionString = versionString.replaceAll("\\s", ""); //remove all whitespace
        final String versionPattern = "[0-9]+\\.[0-9]+\\.[0-9]+";
        Version version = null;

        if (versionString.matches(versionPattern))
        {
            String[] versionStrings = versionString.split("\\."); //it's regex so escape the .

            version = new Version();
            version.majorVersion = Integer.parseInt(versionStrings[0]);
            version.minorVersion = Integer.parseInt(versionStrings[1]);
            version.patchVersion = Integer.parseInt(versionStrings[2]);
        } else
        {
            version = null;
            throw new IllegalArgumentException("'" + versionString
                + "' does not match the major.minor.patch pattern, where major/minor/patch are integers >= 0.");
        }

        return version;
    }

    /**
     * Parses a json representation of a Version and returns it.
     * 
     * @param json
     *            the json representation of the version
     * @return the object representation of a json formatted version, null if there are any parsing errors.
     */
    public static Version fromJson(String json)
    {
        Gson gson = new Gson();
        Version version = null;
        try
        {
            version = gson.fromJson(json, Version.class);
        } catch (JsonSyntaxException e)
        {
            Logger.Log(e);
            e.printStackTrace();
            version = null;
        }
        return version;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(getVersionStringWithName());

        if (getChangeLog() != null)
        {
            builder.append(System.lineSeparator() + System.lineSeparator() + getChangeLog().toString());
        }

        if (getNote() != null && !getNote().isEmpty())
        {
            builder.append(System.lineSeparator() + "Note: " + getNote());
        }

        if (getDownloadLocation() != null && !getDownloadLocation().isEmpty())
        {
            builder.append(System.lineSeparator() + "Download: " + getDownloadLocation());
        }

        return builder.toString();
    }

    private static Version getDebugVersion()
    {
        Version debug = new Version();
        debug.majorVersion = 0;
        debug.minorVersion = 0;
        debug.patchVersion = 0;
        debug.versionName = "Debug";
        return debug;
    }

    /**
     * Attempts to read the version number out of the MANIFEST.MF. If not found then Debug is returned as the version.
     * <p>
     * 
     * @return the full version number of this application
     */
    public static Version getCurrentRunningVersion()
    {
        String versionString = null;
        try
        {
            final Properties pomProperties = new Properties();
            pomProperties.load(Preferences.class.getResourceAsStream("/metadata.prefs"));
            versionString = pomProperties.getProperty("Application-Version");
        } catch (Exception e)
        {
            Logger.Log(e);
        }

        return versionString == null ? Version.getDebugVersion() : Version.fromString(versionString);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + majorVersion;
        result = prime * result + minorVersion;
        result = prime * result + patchVersion;
        result = prime * result + ((versionName == null) ? 0 : versionName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Version)
        {
            return compareTo((Version) obj) == 0;
        } else
        {
            return false;
        }
    }

    public static boolean isDebug()
    {
        return getCurrentRunningVersion().equals(getDebugVersion());
    }

    public boolean isUpToDate(Version latest)
    {
        return this.compareTo(latest) >= 0;
    }
}
