package com.shaneisrael.st.data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
    static Locations locations = new Locations();
    static PrintWriter log;
    public static void Log(Exception ex)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        
        String timestamp = new SimpleDateFormat("MM-dd-yy").format(new Date());
        try
        {
            log = new PrintWriter(locations.getLogDirectory().getAbsolutePath() + "/log " + timestamp + " "+ System.currentTimeMillis() + ".txt");
            
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        log.write("[MESSAGE]");
        log.write("\n"+ex.getLocalizedMessage()+"\n\n");
        log.write("[VERBOSE]");
        log.write("\n"+sw.toString()+"\n");
        log.close();
    }
}
