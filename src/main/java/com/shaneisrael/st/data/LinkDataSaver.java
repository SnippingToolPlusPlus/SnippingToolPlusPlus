package com.shaneisrael.st.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class LinkDataSaver
{
	PrintWriter out;
	File imgurLinksFile = new File(Preferences.DEFAULT_CAPTURE_DIR
			+ "/Uploads/imgur_links.txt");

	public LinkDataSaver(String upLink, String delLink, String title)
	{
		if (imgurLinksFile.exists() == false)
		{

			try
			{
				out = new PrintWriter(new FileOutputStream(imgurLinksFile));
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.println("TITLE - UPLOAD LINK - DELETION LINK\n------------------------------------");
			out.close();
		}
		addLink(upLink, delLink, title);
	}

	public void addLink(String upLink, String delLink, String title)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(
					imgurLinksFile.getAbsolutePath()));
			String line;
			String temp = "";
			while ((line = reader.readLine()) != null)
				temp += line + "\n";
			out = new PrintWriter(imgurLinksFile.getAbsolutePath());
			out.println(temp + title + " - " + upLink + " - " + delLink);
			out.close();
			reader.close();

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
