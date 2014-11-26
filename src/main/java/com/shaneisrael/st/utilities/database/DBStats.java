package com.shaneisrael.st.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.shaneisrael.st.prefs.Preferences;

/**
 * This class contains static methods for sending usage statistics
 * 
 * @author Shane
 * 
 */
public class DBStats
{
    private static PreparedStatement statement;
    private static Connection connect;
    private static ResultSet result;

    private static String key1 = "0";
    private static String key2 = "0";
    private static String rkid;

    /**
     * 
     * @param uplink
     * @param dellink
     * 
     *          Adds the upload link and delete link to the database under the users
     *          keyset, or default if none is set.
     */
    public static void addHistory(String uplink, String dellink)
    {
        /** If tracking not disabled **/
        if (!Preferences.getInstance().isTrackingDisabled())
        {
            connect = DBConnection.getConnection();
            try
            {
                
                getKeySet();
                
                rkid = DBUniqueKey.getUniqueKeyID(key1, key2);
                
                statement = connect
                    .prepareStatement("INSERT INTO upload_history "
                        + "(rkid, upload_link, delete_link, timestamp)"
                        + " VALUES (?, ?, ?, ?)");

                statement.setString(1, rkid);
                statement.setString(2, uplink);
                statement.setString(3, dellink);
                statement.setString(4, new SimpleDateFormat("MM-dd-yy HH:mm:ss").format(new Date()));
                statement.executeUpdate();

                statement.close();
                connect.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
    
    public static void addHistory(ArrayList<String> uploadList, ArrayList<String> deleteList)
    {
        /** If tracking not disabled **/
        if (!Preferences.getInstance().isTrackingDisabled())
        {
            connect = DBConnection.getConnection();
            try
            {
                
                getKeySet();
                
                rkid = DBUniqueKey.getUniqueKeyID(key1, key2);
                
                statement = connect
                    .prepareStatement("INSERT INTO upload_history "
                        + "(rkid, upload_link, delete_link, timestamp)"
                        + " VALUES (?, ?, ?, ?)");
                
                for(int i = 0; i < uploadList.size(); i++)
                {
    
                    statement.setString(1, rkid);
                    statement.setString(2, uploadList.get(i));
                    statement.setString(3, deleteList.get(i));
                    statement.setString(4, new SimpleDateFormat("MM-dd-yy HH:mm:ss").format(new Date()));
                    statement.executeUpdate();
                    
                }
                uploadList.clear();
                deleteList.clear();
                statement.close();
                connect.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
    }

    private static void getKeySet()
    {

        if (DBUniqueKey.isKeysetValid())
        {
            key1 = Preferences.getInstance().getUniqueKey1();
            key2 = Preferences.getInstance().getUniqueKey2();
            if (key1.equals("") || key2.equals(""))
            {
                key1 = "0";
                key2 = "0";
            }
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
