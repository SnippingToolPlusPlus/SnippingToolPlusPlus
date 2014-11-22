package com.shaneisrael.st.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.shaneisrael.st.prefs.Preferences;

/**
 * This class contains static methods for sending usage statistics
 * @author Shane
 *
 */
public class DBStats
{
    private static PreparedStatement statement;
    private static Connection connect;
    
    private static String key1 = "0";
    private static String key2 = "0";
    
    public static void addHistory(String uplink, String dellink)
    {
        connect = DBConnection.getConnection();
        try
        {
            
            statement = connect.prepareStatement("INSERT INTO upload_history VALUES (?, ?, ?, ?, ?)");
            
            getKeySet();
            
            statement.setString(1, key1);
            statement.setString(2, key2);
            statement.setString(3, uplink);
            statement.setString(4, dellink);
            statement.setString(5, new SimpleDateFormat("MM-dd-yy HH:mm:ss").format(new Date()));
            statement.executeUpdate();
            
            statement.close();
            connect.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    private static void getKeySet()
    {
        
        if(DBUniqueKey.isKeysetValid())
        {
            key1 = Preferences.getInstance().getUniqueKey1();
            key2 = Preferences.getInstance().getUniqueKey2();
        }
        else
        {
            /*
             * If the user does not have a valid key set. Use the default key set.
             */
            key1 = "0";
            key2 = "0";
        }
        
    }
}
