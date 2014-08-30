package com.shaneisrael.st.imgur;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shaneisrael.st.Config;
import com.shaneisrael.st.upload.SimpleFileUploader;
import com.shaneisrael.st.upload.UploadListener;

public class ImgurUploader implements UploadListener
{
    private static final Gson gson = new Gson();
    private static final String IMGUR_URI = "https://api.imgur.com/3";
    private static final String CLIENT_ID = "6311d570cd54953";

    private ImgurResponseListener listener;

    public void upload(BufferedImage image, ImgurResponseListener listener)
    {
        upload(saveTemporarily(image), listener);
    }

    public void upload(File imageFile, ImgurResponseListener listener)
    {
        this.listener = listener;
        SimpleFileUploader uploader = new SimpleFileUploader(
            IMGUR_URI + "/image.json",
            imageFile,
            Config.STPP_USER_AGENT,
            CLIENT_ID);
        uploader.addField("description", "Uploaded via " + Config.WEBSITE_URL);
        uploader.uploadAsync(this);
    }

    @Override
    public void onUploadSuccess(String content)
    {
        ImgurResponse response = null;

        try
        {
            response = gson.fromJson(content, ImgurResponse.class);
        } catch (JsonSyntaxException ex)
        {
            listener.onImgurResponseFail(response);
        }

        if (response != null && response.wasSuccessful())
        {
            ImgurImage uploadedImage = gson.fromJson(response.getRawData(), ImgurImage.class);
            if (uploadedImage != null)
            {
                listener.onImgurResponseSuccess(uploadedImage);
            } else
            {
                listener.onImgurResponseFail(response);
            }
        } else
        {
            listener.onImgurResponseFail(response);
        }
    }

    @Override
    public void onUploadFail(int statusCode, String reason)
    {
        System.out.println(statusCode + ": Failed to upload image: " + reason);
        listener.onImgurResponseFail(null);
    }

    private File saveTemporarily(BufferedImage image)
    {
        try
        {
            File file = File.createTempFile("stpp-", "-snip.png");
            ImageIO.write(image, "png", file);

            return file;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
