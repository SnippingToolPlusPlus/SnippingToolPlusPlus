package com.shaneisrael.st.utilities.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection
{
    private static String user;
    private static String password;
    private static String address = "snippingtoolpluspl.us:3306"; //192.168.11.10:3306
    
    public static Connection getConnection()
    {
        Connection connect;
        
        if(user == null || password == null)
        {
            System.out.println("Performing first time decryption...");
            try
            {
                Decryption.getUserDetails();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://"+address+"/stppstats?"
                +"user="+user+"&password="+password);
            
            return connect;
            
        }
        catch(Exception e){e.printStackTrace();}
        
        return null;
    }
    public static void setUser(String usr)
    {
        user = usr;
    }
    public static void setPassword(String pass)
    {
        password = pass;
    }
}
