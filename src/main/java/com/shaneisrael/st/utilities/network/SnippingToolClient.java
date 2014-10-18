package com.shaneisrael.st.utilities.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SnippingToolClient implements Runnable
{
    String message;
    Thread thread;
    
    public SnippingToolClient(String link)
    {
        thread = new Thread(this);
        this.message = link;
        
        thread.start();
    }

    @Override
    public void run()
    {
        String host = "";
        int port = 7777;
        
        try(
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            )
            {
            String reply;
                while((reply = in.readLine()) != null)
                {
                    System.out.println(reply);
                }
                out.println(message);
            } catch (UnknownHostException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
    }

}
