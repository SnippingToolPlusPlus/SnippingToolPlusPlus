package com.shaneisrael.st.imgur;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the imgur image model found at https://api.imgur.com/models/image
 */
public class ImgurImage
{
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("datetime")
    private int uploadDateTime;

    @SerializedName("type")
    private String mimeType;

    @SerializedName("animated")
    private boolean isAnimated;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    @SerializedName("size")
    private int sizeInBytes;

    @SerializedName("views")
    private int views;

    @SerializedName("bandwidth")
    private int bandwidthInBytes;

    @SerializedName("deletehash")
    private String deleteHash;

    @SerializedName("section")
    private String category;

    @SerializedName("link")
    private String link;

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public int getUploadDateTime()
    {
        return uploadDateTime;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public boolean isAnimated()
    {
        return isAnimated;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getSizeInBytes()
    {
        return sizeInBytes;
    }

    public int getViews()
    {
        return views;
    }

    public int getBandwidthInBytes()
    {
        return bandwidthInBytes;
    }

    public String getDeleteHash()
    {
        return deleteHash;
    }

    public String getDeleteLink()
    {
        return "http://imgur.com/delete/" + getDeleteHash();
    }

    public String getCategory()
    {
        return category;
    }

    public String getLink()
    {
        return link;
    }

    @Override
    public String toString()
    {
        return getLink();
    }

}
