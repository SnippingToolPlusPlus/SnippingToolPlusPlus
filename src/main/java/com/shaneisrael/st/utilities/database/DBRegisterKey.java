package com.shaneisrael.st.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class will be used to register a clients two part key. If the
 * two part key combo is already in use in the database then the user
 * will be told to create create a different key. 
 * 
 * Keys ARE NOT passwords. This is just a way to recognize differentiate 
 * users. No information of value will be stored in the database under 
 * these keys. Meaning, if another user tries to use your key pair then
 * they would have access to your PUBLIC upload history and upload
 * stats in general. This information is already public on imgur.
 * 
 * @author Shane
 *
 */
public class DBRegisterKey
{
    private static PreparedStatement statement;
    private static Connection connect;
    private static ResultSet resultSet;
    
    public static boolean register(String key1, String key2)
    {
        if(key1.equals("") || key2.equals(""))
            return true;
        
        connect = DBConnection.getConnection();
        boolean keyExists = false;
        try
        {
            statement = connect.prepareStatement("SELECT key_1, key_2 FROM register_key WHERE key_1=? AND key_2=?");
            
            statement.setString(1, key1);
            statement.setString(2, key2);
            
            resultSet = statement.executeQuery();
            
            /*If we get a row back, then we know the key already exists.*/
            if(resultSet.next())
                keyExists = true;
            else
            {
                keyExists = false;
                statement = connect.prepareStatement("INSERT INTO register_key VALUES ( ?, ?)");
                statement.setString(1, key1);
                statement.setString(2, key2);
                statement.executeUpdate();
                
                System.out.println("Key Set registered!");
            }
            
            statement.close();
            connect.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return keyExists;
    }
}
