package com.shaneisrael.st.utilities.database;

public class DBUniqueKey
{

    private static String key1 = "0";
    private static String key2 = "0";
    
    //When the program runs, keys should be set and validated
    //When the user saves preferences, keys should be set and validated
    public static void setKey(String key1, String key2)
    {
        if(DBUniqueKey.validated(key1, key2))
        {
            DBUniqueKey.key1 = key1;
            DBUniqueKey.key2 = key2;
        }
    }

    private static boolean validated(String key12, String key22)
    {
        //create database connection, check if key set has been registered.
        //if not, keys will use the value 0,0 which is reserved for all 
        //unregistered clients
        return false;
    }
}
