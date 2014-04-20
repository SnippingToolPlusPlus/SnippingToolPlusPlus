package com.shaneisrael.st.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class DataUtils
{
    private String imgur_credit_url = "http://api.imgur.com/2/credits.xml";
    private int uploads_remaining;
    private int uploads_limit;
    String response = "";

    public DataUtils()
    {
        getImgurDataResponse();
    }

    public void getImgurDataResponse()
    {
        try
        {
            URL url = new URL(imgur_credit_url);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = in.readLine()) != null)
                response += line;

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getRemainingUploads()
    {
        try
        {
            String[] temp = response.split("<remaining>");
            temp = temp[1].split("</remaining>");
            return (Integer.parseInt(temp[0]) / 10);
        } catch (Exception e)
        {
            e.printStackTrace();
            return 50;
        }
    }

    public int getRefreshTimeMins()
    {
        try
        {
            String[] temp = response.split("<refresh_in_secs>");
            temp = temp[1].split("</refresh_in_secs>");
            return (Integer.parseInt(temp[0]) / 60);
        } catch (Exception e)
        {
            e.printStackTrace();
            return 60;
        }
    }

    public String getFormat(String format)
    {
        InputStream is = this.getClass().getResourceAsStream("/data/formats.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        try
        {
            while ((line = in.readLine()) != null)
            {

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
