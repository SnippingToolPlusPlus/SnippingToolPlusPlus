package com.shaneisrael.st.data;

/**
 * An enumeration of available Operating Systems and the data that is specific to them.
 * 
 * @author Talon
 * 
 */
public enum OperatingSystem
{
    WINDOWS(
        "\\.snippingtool++\\data\\version5\\",
        "\\Pictures\\SnippingTool++\\"
    ),
    MAC(
        "/Library/Application Support/snippingtool++/data/version5/",
        "/pictures/SnippingTool++/"
    ),
    OTHER(
        "/.snippingtool++/data/version5/",
        "/pictures/SnippingTool++"
    );

    private final String dataPath;

    private final String picturePath;

    OperatingSystem(String dataPath, String picturePath)
    {
        this.dataPath = dataPath;
        this.picturePath = picturePath;
    }

    /**
     * The data directory path is a relative filepath that can be appended to a root directory path (usually the user's $HOME location).
     * 
     * @return the path to the data directory, with trailing slash.
     */
    public String getDataDirectoryPath()
    {
        return dataPath;
    }

    /**
     * The picture directory path is a relative filepath that can be appended to a root directory path (usually the user's $HOME/pictures variable).
     * 
     * @return the path to the picture directory, with trailing slash.
     */
    public String getPictureDirectoryPath()
    {
        return picturePath;
    }

    /**
     * Checks the os.name System property and returns the correct {@link OperatingSystem}.
     * 
     * @return the currently running OS
     */
    public static OperatingSystem getCurrentOS()
    {
        String osName = System.getProperty("os.name");
        OperatingSystem osType = OperatingSystem.WINDOWS;
        if (osName.contains("Windows"))
        {
            osType = WINDOWS;
        } else if (osName.contains("Mac"))
        {
            osType = MAC;
        } else
        {
            osType = OTHER;
        }
        return osType;
    }

    /**
     * Checks if the currently running OS is equal to the specified OS.
     * 
     * @param os
     *            the os to check for
     * @return true if the running OS matches, false otherwise
     */
    public static boolean is(OperatingSystem os)
    {
        return getCurrentOS() == os;
    }

    /**
     * Checks if the currently running OS is any of the specified OS's.
     * 
     * @param os
     *            a list of os's to check for
     * @return true if the running OS is in the list of provides OS's, false otherwise
     */
    public static boolean isAny(OperatingSystem... oses)
    {
        boolean foundMatch = false;
        for (OperatingSystem os : oses)
        {
            foundMatch |= is(os);
        }
        return foundMatch;
    }

    /**
     * A convenience method for calling is(OperatingSystem.WINDOWS);
     * 
     * @return true if running OS is Windows, false otherwise
     */
    public static boolean isWindows()
    {
        return is(WINDOWS);
    }

    @Override
    public String toString()
    {
        return System.getProperty("os.name");
    }
}
