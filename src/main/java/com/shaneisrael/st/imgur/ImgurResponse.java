package com.shaneisrael.st.imgur;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Represents the imgur image model found at https://api.imgur.com/models/basic
 */
public class ImgurResponse
{
    private static final Gson gson = new Gson();

    @SerializedName("data")
    private JsonObject data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("status")
    private int httpStatusCode;

    public ImgurImage getDataAsImage() throws ImgurException
    {
        ImgurImage image = null;
        if (success)
        {
            image = gson.fromJson(data, ImgurImage.class);
            if (image == null)
            {
                throw new ImgurException(
                    "An error occurred while trying to convert the data to an Imgur Image model. Check the json",
                    httpStatusCode);
            }
        } else
        {
            throw new ImgurException("The imgur response was not successful", httpStatusCode);
        }
        return image;
    }

    public int getHttpStatusCode()
    {
        return httpStatusCode;
    }

    public boolean wasSuccessful()
    {
        return success;
    }

    public JsonObject getRawData()
    {
        return data;
    }
}
