package com.shaneisrael.st.data;

import java.io.File;

/**
 * Responsible for building all Paths that the application uses.
 * 
 * Use this whenever a path needs to be built.
 * 
 * Default base path information for each OS can be found in the {@link OperatingSystem} enum.
 * 
 * @author Talon
 * 
 */
public class Locations
{
    private static final String DATA_FOLDER_ROOT = System.getProperty("user.home");
    private static final String PREFERENCES_FILE = "prefs.json";

    private final OperatingSystem operatingSystem;
    private final String dataRoot;

    /**
     * Uses the default SnippingTool++ directory structure.
     * 
     * Data Root is System.getProperty("user.home")
     * 
     * pictures go in root/Pictures/SnippingTool++ preferences go in root/.snippingtool++/prefs.json
     */
    public Locations()
    {
        this(DATA_FOLDER_ROOT);
    }

    /**
     * Lets you override the dataRoot
     * 
     * @param dataRoot
     *            location to use instead of System.getProperty("user.home")
     */
    public Locations(String dataRoot)
    {
        this(dataRoot, OperatingSystem.getCurrentOS());
    }

    /**
     * This uses the default dataRoot but lets you change the operating system.
     * 
     * This is mostly used for internal testing as you usually want the OS set to whatever is running
     * 
     * @param operatingSystem
     */
    public Locations(OperatingSystem operatingSystem)
    {
        this(DATA_FOLDER_ROOT);
    }

    /**
     * Lets you override both the dataRoot and the operating system.
     * 
     * @param dataRoot
     * @param operatingSystem
     */
    public Locations(String dataRoot, OperatingSystem operatingSystem)
    {
        this.dataRoot = dataRoot;
        this.operatingSystem = operatingSystem;
    }

    /**
     * This location is not guaranteed to exist yet.
     * 
     * @return the data directory for storing preferences, no trailing slash.
     */
    public File getDataDirectory()
    {
        return new File(dataRoot, operatingSystem.getDataDirectoryPath());
    }

    /**
     * This location is not guaranteed to exist yet.
     * 
     * @return the directory for storing pictures, no trailing slash.
     */
    public File getPictureDirectory()
    {
        return new File(dataRoot, operatingSystem.getPictureDirectoryPath());
    }

    /**
     * This location is not guaranteed to exist yet.
     * 
     * @return the absolute preferences path for the correct OS, including file name.
     */
    public File getPreferencesFile()
    {
        return new File(getDataDirectory(), PREFERENCES_FILE);
    }
}
