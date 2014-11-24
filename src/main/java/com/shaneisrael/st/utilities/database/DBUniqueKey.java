package com.shaneisrael.st.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.shaneisrael.st.prefs.Preferences;

public class DBUniqueKey
{
    private static PreparedStatement statement;
    private static Connection connect;
    private static ResultSet resultSet;
    
    public static boolean validate(String key1, String key2)
    {
        if(key1.equals("") || key2.equals(""))
            return true;
        
        connect = DBConnection.getConnection();
        boolean valid = true;
        try
        {
            
            statement = connect.prepareStatement("SELECT key_1, key_2 FROM registered_keys WHERE key_1=? AND key_2=?");
            
            statement.setString(1, key1);
            statement.setString(2, key2);
            
            resultSet = statement.executeQuery();
            
            /*If we get a row back, then we know the key already exists.*/
            if(resultSet.next())
            {
                System.out.println("Key Set is valid...");
                valid = true;
            }
            else
            {
                valid = false;
            }
            statement.close();
            connect.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return valid;
    }
    public static String getUniqueKeyID(String key1, String key2)
    {
        try
        {
            connect = DBConnection.getConnection();
            
            statement = connect.prepareStatement("SELECT id FROM registered_keys WHERE"
                + " key_1=? AND key_2=?");
            
            statement.setString(1, key1);
            statement.setString(2, key2);
            
            resultSet = statement.executeQuery();
            
            String id = "0";
           
            /* Get the key id that is linked to their Keyset */
            while(resultSet.next())
                id = resultSet.getString("id");
            
            return id;
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "0";
    }
    public static boolean isKeysetValid()
    {
        String key1 = Preferences.getInstance().getUniqueKey1();
        String key2 =  Preferences.getInstance().getUniqueKey2();
        if(validate(key1,key2))
            return true;
        else
        {
            Thread t = new Thread(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null, "Your Key Set is invalid, please 'validate' your Key Set\n"
                        + "in the preferences!", "Invalid Key Set", JOptionPane.WARNING_MESSAGE);
                }
            });
          t.start();
            return false;
        }
    }
}
