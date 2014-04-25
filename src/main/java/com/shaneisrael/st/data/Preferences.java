package com.shaneisrael.st.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.shaneisrael.st.utilities.version.Version;

/**
 * 
 * @author Shane
 * 
 *         This class file simply creates and checks directories. performs first time setups and sets defaults.
 * 
 */

public class Preferences
{
    /*
     * 5.2.2
     *
     *Updates
     *=======
     *
     *Bugs
     *====
     *No longer submits multiple times when using the hotkey in the editor
     *Typed text does not disappear when you change the tool
     *Changing the color sets the transparency to your pre-defined correctly now.
     * 
     * TODO ==== Get multi-snippet capture working.
     */

    public static long TOTAL_SAVED_UPLOADS = 0;
    public static String DEFAULT_CAPTURE_DIR = System.getProperty("user.home") + "/pictures/SnippingTool++/";

    public static String VERSION = "";
    public static boolean EDITING_ENABLED = true;
    public static boolean AUTO_SAVE_UPLOADS = true;
    public static long DEFAULT_UPLOAD_PROVIDER = 0; // 0 = imgur, 1 = minus
    public static boolean FORCE_MULTI_CAPTURE = false;
    public static long DEFAULT_EDITING_TOOL = 0; // pencil by default

    private static JSONObject pref; // outputs
    private static JSONObject prefIn;

    private File dataFolder;
    private File picturesFolder;

    private static Locations locations = new Locations();

    public Preferences()
    {

        dataFolder = locations.getDataDirectory();

        picturesFolder = locations.getPictureDirectory();

        pref = new JSONObject();
        checkDirectories();
    }

    public static void loadPreferences()
    {
        /*
         * Load the preferences from the json file and set the Preferences Class
         * constants
         * 
         * preferences are not saving and loading correctly NOTE: possibly fixed
         * now
         */
        System.out.println("Loading preferences...");
        JSONParser prefParser = new JSONParser();
        try
        {
            Object obj;
            obj = prefParser.parse(new FileReader(locations.getPreferencesFile()));
            prefIn = (JSONObject) obj;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        DEFAULT_CAPTURE_DIR = (String) prefIn.get("user_capture_dir");
        DEFAULT_EDITING_TOOL = (Long) prefIn.get("default.editing.tool");
        DEFAULT_UPLOAD_PROVIDER = (Long) prefIn.get("default.upload.provider");
        EDITING_ENABLED = (Boolean) prefIn.get("editing.enabled");
        FORCE_MULTI_CAPTURE = (Boolean) prefIn.get("force.multi.capture");
        AUTO_SAVE_UPLOADS = (Boolean) prefIn.get("auto.save.uploads");
    }

    private void checkDirectories()
    {
        // if the data folder does not exist, create
        // it (first time setup)
        if (!dataFolder.exists())
        {
            System.out.println("Version mismatch... \nPerforming first time setup...");
            setupDirectories();
        } else
        {
            loadPreferences();

            if (!VERSION.equals(prefIn.get("version")))
            { // if different version
                setupDirectories();
            }
        }

        // so it will make sure that the pictures folder is there in osx
        if (!picturesFolder.exists() && OperatingSystem.is(OperatingSystem.MAC))
        {
            System.out.println("No pictures folder existes... \nCreating folder");
            picturesFolder.mkdirs();
            new File(DEFAULT_CAPTURE_DIR + "/Captures/").mkdir();
            new File(DEFAULT_CAPTURE_DIR + "/Uploads/").mkdir();
        }
    }

    private void setupDirectories()
    {
        //create the data directory, default snipping tool capture directory
        dataFolder.mkdirs(); // changed to mkdirs
        createPreferencesFile();
    }

    @SuppressWarnings("unchecked")
    private void createPreferencesFile()
    {
        pref.put("version", Version.getCurrentRunningVersion().getVersionString());
        pref.put("user_capture_dir", DEFAULT_CAPTURE_DIR);
        pref.put("editing.enabled", EDITING_ENABLED);
        pref.put("default.upload.provider", DEFAULT_UPLOAD_PROVIDER);
        pref.put("auto.save.uploads", AUTO_SAVE_UPLOADS);
        pref.put("force.multi.capture", FORCE_MULTI_CAPTURE);
        pref.put("default.editing.tool", DEFAULT_EDITING_TOOL);
        try
        {
            FileWriter file = new FileWriter(locations.getPreferencesFile());
            file.write(pref.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("=====PREFERENCES======\n" + pref);
    }

    @SuppressWarnings("unchecked")
    public static void updatePreferences(PreferencesUI ui)
    {
        pref.put("user_capture_dir", ui.directoryField.getText());
        pref.put("editing.enabled", ui.chckbxEnableEditor.isSelected());
        pref.put("default.upload.provider", 0);
        pref.put("auto.save.uploads", ui.chckbxAutosaveUploads.isSelected());
        pref.put("force.multi.capture", ui.chckbxForceMultisnippetCapture.isSelected());
        pref.put("default.editing.tool", ui.toolBox.getSelectedIndex());
        try
        {
            FileWriter file = new FileWriter(locations.getPreferencesFile());
            file.write(pref.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("=====PREFERENCES======\n" + pref);
    }
}
