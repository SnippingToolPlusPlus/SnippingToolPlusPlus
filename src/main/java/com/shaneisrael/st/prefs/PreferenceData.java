package com.shaneisrael.st.prefs;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public final class PreferenceData
{
    @SerializedName("capture_directory_root")
    String captureDirectoryRoot;

    @SerializedName("autosave_enabled")
    boolean autoSaveEnabled;

    @SerializedName("editor")
    EditorPreferences editor;

    @SerializedName("upload_quality")
    public float uploadQuality;
    
    /** Begin FTP Data **/
    
    @SerializedName("ftp_host")
    public String ftpHost;
    
    @SerializedName("ftp_user")
    public String ftpUser;
    
    @SerializedName("ftp_password")
    public String ftpPassword;
    
    @SerializedName("ftp_port")
    public String ftpPort;
    
    @SerializedName("ftp_path")
    public String ftpPath;
    
    @SerializedName("ftp_upload_always")
    boolean ftpUploadAlways;
    
    PreferenceData()
    {
        editor = new EditorPreferences();
    }

    /**
     * @return the captureDirectoryRoot
     */
    String getCaptureDirectoryRoot()
    {
        return captureDirectoryRoot;
    }

    /**
     * @return the autoSaveEnabled
     */
    boolean isAutoSaveEnabled()
    {
        return autoSaveEnabled;
    }

    /**
     * @return the editor
     */
    EditorPreferences getEditor()
    {
        return editor;
    }

    /**
     * @param captureDirectoryRoot
     *            the captureDirectoryRoot to set
     */
    void setCaptureDirectoryRoot(String captureDirectoryRoot)
    {
        this.captureDirectoryRoot = captureDirectoryRoot;
    }

    /**
     * @param autoSaveEnabled
     *            the autoSaveEnabled to set
     */
    void setAutoSaveEnabled(boolean autoSaveEnabled)
    {
        this.autoSaveEnabled = autoSaveEnabled;
    }
    
    /**
     * 
     * @param setUploadQuality
     *          the quality level of the uploads
     */
    void setUploadQuality(float quality)
    {
        this.uploadQuality = quality;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    void setEditorEnabled(boolean enabled)
    {
        getEditor().enabled = enabled;
    }

    /**
     * @param defaultTool
     *            the defaultTool to set
     */
    void setDefaultTool(long defaultTool)
    {
        getEditor().defaultTool = defaultTool;
    }
    
    /**
     * @param host
     *          the ftp server url
     */
    void setFTPHost(String host)
    {
        this.ftpHost = host;
    }
    
    /**
     * @param user
     *          the desired user to login as
     */
    void setFTPUser(String user)
    {
        this.ftpUser = user;
    }
    
    /**
     * @param pass
     *          the password for the specified user
     */
    void setFTPPassword(String pass)
    {
        this.ftpPassword = pass;
    }
    
    /**
     * @param port
     *          the port the ftp server listens on   
     */
    void setFTPPort(String port)
    {
        this.ftpPort = port;
    }
    
    /**
     * @param path
     *          the desired path uploaded images will be saved to
     */
    void setFTPPath(String path)
    {
        this.ftpPath = path;
    }

    /**
     * @param always
     *          should every upload be also uploaded to the ftp server
     */
    void setFTPUploadAlways(boolean always)
    {
        this.ftpUploadAlways = always;
    }
    /**
     * @return the enabled
     */
    boolean isEditorEnabled()
    {
        return getEditor().enabled;
    }
    
    /**
     * @return the quality setting of uploads
     */
    float getUploadQuality()
    {
        return uploadQuality;
    }
    /**
     * @return the defaultTool
     */
    long getDefaultTool()
    {
        return getEditor().defaultTool;
    }

    /**
     * @return the ftp host url
     */
    String getFTPHost()
    {
        return ftpHost;
    }
    
    /**
     * @return the ftp user
     */
    String getFTPUser()
    {
        return ftpUser;
    }
    
    /**
     * @return  the ftp user password
     */
    String getFTPPassword()
    {
        return ftpPassword;
    }
    
    /**
     * @return the ftp server port
     */
    String getFTPPort()
    {
        return ftpPort;
    }
    
    /**
     * @return the image save location
     */
    String getFTPPath()
    {
        return ftpPath;
    }
    
    /**
     * @return is every upload sent to the ftp server
     */
    boolean getFTPUploadAlways()
    {
        return ftpUploadAlways;
    }
    class EditorPreferences
    {
        @SerializedName("enabled")
        boolean enabled;

        @SerializedName("default_tool")
        long defaultTool;

    }

    @Override
    public String toString()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
