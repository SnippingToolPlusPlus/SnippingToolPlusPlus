package com.shaneisrael.st.upload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shaneisrael.st.Config;

public class ImgurUploader
{
    private static final String IMGUR_URI = "https://api.imgur.com/3";
    public static final String CLIENT_ID = "6311d570cd54953";

    private final HTTPFileUploader imgur;

    public ImgurUploader(String userAgent)
    {
        imgur = new HTTPFileUploader(IMGUR_URI + "/image.json", userAgent);
        imgur.setHeader("Authorization", "Client-ID " + CLIENT_ID);
        imgur.setField("type", "file");
        imgur.setField("description", "Uploaded via " + Config.WEBSITE_URL);
    }

    public String upload(BufferedImage image) throws IOException
    {
        return upload(saveTemporarily(image));
    }

    public String upload(File imageFile) throws IOException
    {
        imgur.setFile("image", imageFile);
        imgur.startUpload(); //blocking call
        String imgurJson = imgur.finish();
        Gson gson = new Gson();
        JsonObject imgurResponse = gson.fromJson(imgurJson, JsonObject.class);
        String link = imgurResponse.get("data").getAsJsonObject().get("link").getAsString();
        return link;
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
