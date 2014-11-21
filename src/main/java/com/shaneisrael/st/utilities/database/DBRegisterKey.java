package com.shaneisrael.st.utilities.database;

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

    public static boolean register()
    {
        boolean success = false;
        
        //logic and things
        
        if(success)
            return success;
        else
            return false;
    }
}
