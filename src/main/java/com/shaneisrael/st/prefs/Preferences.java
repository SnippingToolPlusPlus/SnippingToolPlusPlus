package com.shaneisrael.st.prefs;

import java.io.File;
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
        System.out.println(preferences.getUploadQuality());
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
        preferences.setUploadQuality(1f);
        preferences.setCaptureDirectoryRoot(locations.getPictureDirectory().getAbsolutePath());
        save();
    }

    public String getCaptureDirectoryRoot()
    {
        refresh();
        return preferences.getCaptureDirectoryRoot();
    }
    public File getUploadsDirectoryRoot()
    {
    	return new File(preferences.getCaptureDirectoryRoot() + locations.getUploadsDirectory());
    }
    public File getSavesDirectoryRoot()
    {
    	return new File(preferences.getCaptureDirectoryRoot() + locations.getSavesDirectory());
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
		if(!getUploadsDirectoryRoot().exists())
			getUploadsDirectoryRoot().mkdirs();
		if(!getSavesDirectoryRoot().exists())
			getSavesDirectoryRoot().mkdirs();

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
    
    /**
     * @param uploadQuality
     *            the uploadQuality to set
     */
    public void setUploadQuality(float f)
    {
        preferences.uploadQuality = f;
        save();
    }

    /**
     * 
     * @return the current upload quality setting
     */
    public float getUploadQuality()
    {
        return preferences.getUploadQuality();
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
    
    /**
     * @param host
     *          the ftp server url
     */
    public void setFTPHost(String host)
    {
        preferences.ftpHost = host;
        save();
    }
    
    /**
     * @param user
     *          the desired user to login as
     */
    public void setFTPUser(String user)
    {
        preferences.ftpUser = user;
        save();
    }
    
    /**
     * @param password
     *          the password for the specified user
     */
    public void setFTPPassword(String password)
    {
        preferences.ftpPassword = password;
        save();
    }
    
    /**
     * @param port
     *          the port the ftp server listens on   
     */
    public void setFTPPort(String port)
    {
        preferences.ftpPort = port;
        save();
    }
    
    /**
     * @param path
     *          the desired path uploaded images will be saved to
     */
    public void setFTPPath(String path)
    {
        preferences.ftpPath = path;
        save();
    }
    
    /**
     * @param always
     *          should every upload be also uploaded to the ftp server
     */
    public void setFTPUploadAlways(boolean always)
    {
        preferences.ftpUploadAlways = always;
        save();
    }
    
    /**
     * 
     * @param generateStamp
     *          should a time stamp be generated as the file name
     */
    public void setFTPGenerateTimestamp(boolean generateStamp)
    {
        preferences.ftpGenerateTimestamp = generateStamp;
        save();
    }
    /**
     * @return the ftp host url
     */
    public String getFTPHost()
    {
        return preferences.getFTPHost();
    }
    
    /**
     * @return the ftp user
     */
    public String getFTPUser()
    {
        return preferences.getFTPUser();
    }
    
    /**
     * @return  the ftp user password
     */
    public String getFTPPassword()
    {
        return preferences.getFTPPassword();
    }
    
    /**
     * @return the ftp server port
     */
    public String getFTPPort()
    {
        return preferences.getFTPPort();
    }
    
    /**
     * @return the image save location
     */
    public String getFTPPath()
    {
        return preferences.getFTPPath();
    }
    
    /**
     * @return is every upload sent to the ftp server
     */
    public boolean getFTPUploadAlways()
    {
        return preferences.getFTPUploadAlways();
    }

    /**
     * @return is a time stamp generated as the file name
     */
    public boolean getFTPGenerateTimestamp()
    {
        return preferences.getFTPGenerateTimestamp();
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

    public boolean isFTPReady()
    {
        if(getFTPUser().equals(""))
            return false;
        else if(getFTPPassword().equals(""))
            return false;
        else if(getFTPHost().equals(""))
            return false;
        else if(getFTPPort().equals(""))
            return false;
        
        return true;
    }
}
