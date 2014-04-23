package com.shaneisrael.st.utilities.version;

import com.google.gson.annotations.SerializedName;

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
     * Changed when the user will have to manually do something due to incompatibilites with previous versions.
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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Version " + getVersionString());

        if (getVersionName() != null && !getVersionName().isEmpty())
        {
            builder.append(" [" + getVersionName() + "]");
        }

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
}
