package com.shaneisrael.st.utilities.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.shaneisrael.st.utilities.ClipboardUtilities;

public class IPListener implements Runnable
{

    @Override
    public void run()
    {
        System.out.println("listening started");
        
        try(
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            String input, message = null;
            while((input = in.readLine()) != null)
            {
                message += input;
            }
            ClipboardUtilities.setClipboard(message);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("listening stopped");
    }
    
}
