package com.shaneisrael.st.prefs;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shaneisrael.st.data.Locations;
import com.shaneisrael.st.utilities.FileReader;

public class Preferences
{
    public static long TOTAL_SAVED_UPLOADS = 0;
    private static Preferences instance;

    private final Locations locations;
    private String jsonData;
    private PreferenceData preferences;

    private Preferences(Locations locations) throws PreferencesException
    {
        this.locations = locations;
        if (locations.getPreferencesFile().exists() && locations.getPreferencesFile().isFile())
        {
            try
            {
                jsonData = FileReader.readFile(locations.getPreferencesFile().getAbsolutePath());
            } catch (IOException e)
            {
                throw new PreferencesException("Error reading preferences with Locations: " + locations.toString()
                    + ": " + e.getMessage());
            }
            init();
        } else
        {
            setDefaultPreferences();
        }
    }

    private void init()
    {
        Gson gson = new Gson();
        preferences = gson.fromJson(jsonData, PreferenceData.class);
    }

    public void save()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(preferences);
        try
        {
            locations.getPreferencesFile().mkdirs();
            FileReader.writeFile(locations.getPreferencesFile().getAbsolutePath(), json);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reloads settings from disk
     */
    public void refresh()
    {
        try
        {
            jsonData = FileReader.readFile(locations.getPreferencesFile().getAbsolutePath());
        } catch (IOException e)
        {
            e.printStackTrace(); //shouldn't happen
        }
        Gson gson = new Gson();
        preferences = gson.fromJson(jsonData, PreferenceData.class);
    }

    public void setDefaultPreferences()
    {
        System.out.println("Could not locate existing preferences. Creating defaults...");
        System.out.println("Creating " + locations.getDataDirectory().getAbsolutePath());
        System.out.println("Creating " + locations.getPictureDirectory().getAbsolutePath());
        System.out.println("Creating " + locations.getPreferencesFile().getAbsolutePath());

        locations.getDataDirectory().mkdirs();
        locations.getUploadsDirectory().mkdirs();
        locations.getSavesDirectory().mkdirs();
        
        try
        {
            locations.getPreferencesFile().createNewFile();
        } catch (IOException e)
        {
            System.out.println("Could not create default settings at "
                + locations.getPreferencesFile().getAbsolutePath());
            e.printStackTrace();
        }

        preferences = new PreferenceData();
        preferences.setAutoSaveEnabled(true);
        preferences.setEditorEnabled(true);
        preferences.setDefaultTool(0);
        preferences.setCaptureDirectoryRoot(locations.getPictureDirectory().getAbsolutePath());
        save();
    }

    public String getCaptureDirectoryRoot()
    {
        refresh();
        return preferences.getCaptureDirectoryRoot();
    }

    /**
     * @param captureDirectoryRoot
     *            the captureDirectoryRoot to set
     */
    public void setCaptureDirectoryRoot(String captureDirectoryRoot)
    {
        preferences.captureDirectoryRoot = captureDirectoryRoot;
        save();
    }
    
    /**
     * @return Checks if the capture directories exist. Creates them if they don't.
     */
	public void checkDirectories() 
	{
		if(!locations.getDataDirectory().exists())
			setDefaultPreferences();
		if(!locations.getUploadsDirectory().exists())
			locations.getUploadsDirectory().mkdirs();
		if(!locations.getSavesDirectory().exists())
			locations.getSavesDirectory().mkdirs();
	}

    public boolean isAutoSaveEnabled()
    {
        refresh();
        return preferences.isAutoSaveEnabled();
    }

    /**
     * @param autoSaveEnabled
     *            the autoSaveEnabled to set
     */
    public void setAutoSaveEnabled(boolean autoSaveEnabled)
    {
        preferences.autoSaveEnabled = autoSaveEnabled;
        save();
    }

    public boolean isEditorEnabled()
    {
        refresh();
        return preferences.isEditorEnabled();
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEditorEnabled(boolean enabled)
    {
        preferences.getEditor().enabled = enabled;
        save();
    }

    public long getDefaultTool()
    {
        refresh();
        return preferences.getDefaultTool();
    }

    /**
     * @param defaultTool
     *            the defaultTool to set
     */
    public void setDefaultTool(long defaultTool)
    {
        preferences.getEditor().defaultTool = defaultTool;
        save();
    }

    public static Preferences getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new Preferences(new Locations());
            } catch (PreferencesException e)
            {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
