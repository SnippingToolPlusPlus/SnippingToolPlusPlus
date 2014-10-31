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
     * @return the enabled
     */
    boolean isEditorEnabled()
    {
        return getEditor().enabled;
    }
    
    /**
     * 
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
